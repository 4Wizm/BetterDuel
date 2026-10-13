package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Arena;
import com.wizm.betterduel.objects.Duel;
import com.wizm.betterduel.objects.DuelPlayer;
import com.wizm.betterduel.objects.Kit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DuelManager {

    private final BetterDuel plugin;
    private final List<Duel> duelliAttivi = new ArrayList<>();
    private final Map<String, Arena> arene = new HashMap<>();
    private final Random random = new Random();

    public DuelManager(BetterDuel plugin) {
        this.plugin = plugin;
        caricaArene();
    }

    public void caricaArene() {
        arene.clear();
        ConfigurationSection sez = plugin.getConfig().getConfigurationSection("arena.elenco");
        if (sez == null) return;
        World mondo = Bukkit.getWorld(plugin.getConfig().getString("impostazioni.mondo-duelli", "betterduel_arena"));
        if (mondo == null) return;
        for (String nome : sez.getKeys(false)) {
            double x1 = sez.getDouble(nome + ".spawn1.x", 0.5);
            double y1 = sez.getDouble(nome + ".spawn1.y", 65.0);
            double z1 = sez.getDouble(nome + ".spawn1.z", 0.5);
            float yaw1 = (float) sez.getDouble(nome + ".spawn1.yaw", 0.0);
            float pitch1 = (float) sez.getDouble(nome + ".spawn1.pitch", 0.0);
            Location s1 = new Location(mondo, x1, y1, z1, yaw1, pitch1);

            double x2 = sez.getDouble(nome + ".spawn2.x", 0.5);
            double y2 = sez.getDouble(nome + ".spawn2.y", 65.0);
            double z2 = sez.getDouble(nome + ".spawn2.z", 15.5);
            float yaw2 = (float) sez.getDouble(nome + ".spawn2.yaw", 180.0);
            float pitch2 = (float) sez.getDouble(nome + ".spawn2.pitch", 0.0);
            Location s2 = new Location(mondo, x2, y2, z2, yaw2, pitch2);

            arene.put(nome.toLowerCase(), new Arena(nome.toLowerCase(), s1, s2));
        }
        plugin.getLogger().info("Caricate " + arene.size() + " arene.");
    }

    public Arena getArenaLibera() {
        List<Arena> libere = new ArrayList<>();
        for (Arena a : arene.values()) {
            if (!a.isOccupata()) libere.add(a);
        }
        if (libere.isEmpty()) return null;
        return libere.get(random.nextInt(libere.size()));
    }

    public Map<String, Arena> getArene() { return arene; }

    public void avviaDuello(Player p1, Player p2, Kit kit) {
        World mondo = Bukkit.getWorld(plugin.getConfig().getString("impostazioni.mondo-duelli", "betterduel_arena"));
        if (mondo == null) {
            p1.sendMessage(plugin.msg("database-errore"));
            return;
        }

        Arena arena = getArenaLibera();
        if (arena == null) {
            p1.sendMessage(BetterDuel.color("&cNessuna arena disponibile, riprova!"));
            p2.sendMessage(BetterDuel.color("&cNessuna arena disponibile, riprova!"));
            return;
        }
        arena.setOccupata(true);

        Location spawn1 = arena.getSpawn1();
        Location spawn2 = arena.getSpawn2();

        DuelPlayer dp1 = new DuelPlayer(p1);
        DuelPlayer dp2 = new DuelPlayer(p2);
        Duel duel = new Duel(dp1, dp2, kit, spawn1, spawn2, arena);
        duelliAttivi.add(duel);

        preparaGiocatore(p1, spawn1, kit);
        preparaGiocatore(p2, spawn2, kit);

        p1.sendMessage(plugin.msg("avversario-trovato").replace("%avversario%", p2.getName()));
        p2.sendMessage(plugin.msg("avversario-trovato").replace("%avversario%", p1.getName()));

        int secondi = plugin.getConfig().getInt("arena.blocco-secondi", 3);
        String msgBlocco = plugin.getConfig().getString("arena.blocco-messaggio", "&eIl duello inizia tra &c%tempo% &esecondi...");
        String msgVia = plugin.getConfig().getString("arena.blocco-messaggio-fine", "&a&lVIA!");

        new BukkitRunnable() {
            int count = secondi;
            @Override
            public void run() {
                if (count > 0) {
                    String titolo = BetterDuel.color(msgBlocco.replace("%tempo%", String.valueOf(count)));
                    p1.sendTitle(titolo, "", 0, 25, 5);
                    p2.sendTitle(titolo, "", 0, 25, 5);
                    count--;
                } else {
                    p1.sendTitle(BetterDuel.color(msgVia), "", 0, 25, 5);
                    p2.sendTitle(BetterDuel.color(msgVia), "", 0, 25, 5);
                    p1.sendMessage(plugin.msg("duello-iniziato"));
                    p2.sendMessage(plugin.msg("duello-iniziato"));
                    duel.setPvpAttivo(true);
                    plugin.getScoreboardManager().crea(p1, p2, kit);
                    plugin.getScoreboardManager().crea(p2, p1, kit);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void preparaGiocatore(Player p, Location spawn, Kit kit) {
        p.teleport(spawn);
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setFireTicks(0);
        for (PotionEffect e : p.getActivePotionEffects()) p.removePotionEffect(e.getType());
        plugin.getKitManager().daiKit(p, kit);
    }

    public boolean inDuello(Player p) {
        for (Duel d : duelliAttivi) {
            if (d.contiene(p) && !d.isTerminato()) return true;
        }
        return false;
    }

    public Duel getDuello(Player p) {
        for (Duel d : duelliAttivi) {
            if (d.contiene(p) && !d.isTerminato()) return d;
        }
        return null;
    }

    public void terminaDuello(Duel duel, Player vincitore, Player perdente) {
        if (duel.isTerminato()) return;
        duel.setTerminato(true);
        duelliAttivi.remove(duel);

        if (duel.getArena() != null) duel.getArena().setOccupata(false);

        plugin.getScoreboardManager().rimuovi(duel.getP1().getPlayer());
        plugin.getScoreboardManager().rimuovi(duel.getP2().getPlayer());

        if (vincitore != null && perdente != null) {
            plugin.getStatsManager().aggiungiVittoria(vincitore);
            plugin.getStatsManager().aggiungiSconfitta(perdente);
            plugin.getStatsManager().aggiungiKill(vincitore);

            vincitore.sendMessage(plugin.msg("duello-finito-vincitore").replace("%perdente%", perdente.getName()));
            perdente.sendMessage(plugin.msg("duello-finito-perdente").replace("%vincitore%", vincitore.getName()));

            String titoloV = plugin.getConfig().getString("titoli.vittoria.titolo", "&aVITTORIA");
            String subV = plugin.getConfig().getString("titoli.vittoria.sottotitolo", "");
            int fi = plugin.getConfig().getInt("titoli.vittoria.fade-in", 10);
            int st = plugin.getConfig().getInt("titoli.vittoria.stay", 60);
            int fo = plugin.getConfig().getInt("titoli.vittoria.fade-out", 10);
            vincitore.sendTitle(BetterDuel.color(titoloV), BetterDuel.color(subV), fi, st, fo);

            String titoloS = plugin.getConfig().getString("titoli.sconfitta.titolo", "&cSCONFITTA");
            String subS = plugin.getConfig().getString("titoli.sconfitta.sottotitolo", "");
            perdente.sendTitle(BetterDuel.color(titoloS), BetterDuel.color(subS), fi, st, fo);

            assegnaRicompensa(vincitore);
        }

        int secondiRitorno = plugin.getConfig().getInt("impostazioni.ritorno-dopo-secondi", 2);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            duel.getP1().ripristina();
            duel.getP2().ripristina();
        }, 20L * secondiRitorno);
    }

    public void terminaTuttiIDuelli() {
        for (Duel d : new ArrayList<>(duelliAttivi)) {
            d.setTerminato(true);
            if (d.getArena() != null) d.getArena().setOccupata(false);
            d.getP1().ripristina();
            d.getP2().ripristina();
        }
        duelliAttivi.clear();
    }

    public void assegnaRicompensa(Player vincitore) {
        if (!plugin.getConfig().getBoolean("ricompense.abilitato", true)) return;
        List<String> comandi = plugin.getConfig().getStringList("ricompense.comandi");
        for (String cmd : comandi) {
            String finale = cmd.replace("%player%", vincitore.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finale);
        }
        List<String> msgs = plugin.getConfig().getStringList("ricompense.messaggio");
        for (String m : msgs) {
            vincitore.sendMessage(BetterDuel.color(m.replace("%player%", vincitore.getName())));
        }
    }

    public void giocatoreUscito(Player p) {
        Duel d = getDuello(p);
        if (d == null) return;
        Player avversario = d.getAvversario(p).getPlayer();
        if (avversario != null && avversario.isOnline()) {
            avversario.sendMessage(plugin.msg("avversario-uscito"));
            terminaDuello(d, avversario, p);
        } else {
            terminaDuello(d, null, null);
        }
    }

    public boolean puoFarePvp(Player p) {
        Duel d = getDuello(p);
        return d != null && d.isPvpAttivo();
    }
}