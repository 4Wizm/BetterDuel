package com.wizm.betterduel.commands;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Kit;
import com.wizm.betterduel.objects.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComandoDuel implements CommandExecutor, TabCompleter {

    private final BetterDuel plugin;

    public ComandoDuel(BetterDuel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("solo-giocatori"));
            return true;
        }
        Player p = (Player) sender;

        if (args.length == 0) {
            if (plugin.getDuelManager().inDuello(p)) {
                p.sendMessage(plugin.msg("gia-in-duello"));
                return true;
            }
            plugin.getMenuDuelli().apri(p);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("menu")) {
            plugin.getMenuDuelli().apri(p);
            return true;
        }

        if (sub.equals("setspawn")) {
            if (!p.hasPermission("betterduel.admin")) {
                p.sendMessage(plugin.msg("no-permesso"));
                return true;
            }
            if (args.length < 3) {
                p.sendMessage(BetterDuel.color("&cUso: /duel setspawn <1|2> <nome-arena>"));
                return true;
            }
            String tipo = args[1];
            if (!tipo.equals("1") && !tipo.equals("2")) {
                p.sendMessage(BetterDuel.color("&cIl tipo deve essere 1 o 2!"));
                return true;
            }
            String nomeArena = args[2].toLowerCase();
            Location loc = p.getLocation();
            World mondoDuelli = Bukkit.getWorld(plugin.getConfig().getString("impostazioni.mondo-duelli", "betterduel_arena"));

            FileConfiguration cfg = plugin.getConfig();
            String base = "arena.elenco." + nomeArena + ".spawn" + tipo + ".";
            cfg.set(base + "x", loc.getX());
            cfg.set(base + "y", loc.getY());
            cfg.set(base + "z", loc.getZ());
            cfg.set(base + "yaw", (double) loc.getYaw());
            cfg.set(base + "pitch", (double) loc.getPitch());

            try {
                cfg.save(new File(plugin.getDataFolder(), "config.yml"));
                plugin.getDuelManager().caricaArene();
                p.sendMessage(plugin.msg("setspawn-usato")
                        .replace("%tipo%", tipo)
                        .replace("%arena%", nomeArena));
            } catch (IOException e) {
                p.sendMessage(BetterDuel.color("&cErrore salvataggio config!"));
                e.printStackTrace();
            }
            return true;
        }

        if (sub.equals("queue")) {
            if (!p.hasPermission("betterduel.queue")) {
                p.sendMessage(plugin.msg("no-permesso"));
                return true;
            }
            if (args.length < 2) {
                p.sendMessage(BetterDuel.color("&cUso: /duel queue <kit>"));
                return true;
            }
            Kit kit = plugin.getKitManager().getKit(args[1]);
            if (kit == null) {
                p.sendMessage(plugin.msg("kit-non-valido"));
                return true;
            }
            if (!plugin.getKitManager().haPermesso(p, kit)) {
                p.sendMessage(plugin.msg("no-permesso"));
                return true;
            }
            plugin.getQueueManager().aggiungi(p, kit.getId());
            return true;
        }

        if (sub.equals("leave") || sub.equals("quit")) {
            if (plugin.getQueueManager().inCoda(p)) {
                plugin.getQueueManager().rimuovi(p);
                p.sendMessage(plugin.msg("uscito-coda"));
            } else if (plugin.getDuelManager().inDuello(p)) {
                plugin.getDuelManager().giocatoreUscito(p);
            } else {
                p.sendMessage(plugin.msg("non-in-duello"));
            }
            return true;
        }

        if (sub.equals("stats")) {
            Player target = p;
            if (args.length >= 2) {
                if (!p.hasPermission("betterduel.stats.others")) {
                    p.sendMessage(plugin.msg("no-permesso"));
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    p.sendMessage(plugin.msg("giocatore-non-trovato"));
                    return true;
                }
            }
            mostraStats(p, target);
            return true;
        }

        if (sub.equals("reload")) {
            if (!p.hasPermission("betterduel.admin")) {
                p.sendMessage(plugin.msg("no-permesso"));
                return true;
            }
            plugin.reloadConfig();
            plugin.getKitManager().caricaKits();
            plugin.getDuelManager().caricaArene();
            p.sendMessage(plugin.msg("ricarica-config"));
            return true;
        }

        if (sub.equals("kits")) {
            p.sendMessage(BetterDuel.color("&6&lKit disponibili:"));
            for (Kit k : plugin.getKitManager().getKits().values()) {
                String stato = plugin.getKitManager().haPermesso(p, k) ? "&a[OK]" : "&c[X]";
                p.sendMessage(BetterDuel.color(stato + " &e" + k.getId() + " &7- " + k.getNome()));
            }
            return true;
        }

        mostraAiuto(p);
        return true;
    }

    private void mostraAiuto(Player p) {
        p.sendMessage(BetterDuel.color("&6&l=== BetterDuel v1.0.1 ==="));
        p.sendMessage(BetterDuel.color("&e/duel &7- Apre il menu duelli"));
        p.sendMessage(BetterDuel.color("&e/duels &7- Apre il menu duelli"));
        p.sendMessage(BetterDuel.color("&e/duel queue <kit> &7- Entra in coda"));
        p.sendMessage(BetterDuel.color("&e/duel leave &7- Esci"));
        p.sendMessage(BetterDuel.color("&e/duel stats [player] &7- Statistiche"));
        p.sendMessage(BetterDuel.color("&e/duel kits &7- Lista kit"));
        p.sendMessage(BetterDuel.color("&e/duel setspawn <1^|2> <arena> &7- Setta spawn"));
        p.sendMessage(BetterDuel.color("&e/duel reload &7- Ricarica config"));
        p.sendMessage(BetterDuel.color("&e/duelleaderboard &7- Classifica"));
    }

    private void mostraStats(Player p, Player target) {
        Stats s = plugin.getStatsManager().getStats(target);
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-titolo").replace("%giocatore%", target.getName())));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-duelli").replace("%duelli%", String.valueOf(s.getDuelli()))));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-vittorie").replace("%vittorie%", String.valueOf(s.getVittorie()))));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-sconfitte").replace("%sconfitte%", String.valueOf(s.getSconfitte()))));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-kill").replace("%kill%", String.valueOf(s.getKill()))));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-death").replace("%death%", String.valueOf(s.getDeath()))));
        p.sendMessage(BetterDuel.color(plugin.msg("statistiche-kdr").replace("%kdr%", String.valueOf(s.getKdr()))));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Arrays.asList("menu", "queue", "leave", "stats", "kits", "setspawn", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("queue")) {
            list.addAll(plugin.getKitManager().getKits().keySet());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
            for (Player p : Bukkit.getOnlinePlayers()) list.add(p.getName());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setspawn")) {
            list.addAll(Arrays.asList("1", "2"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setspawn")) {
            ConfigurationSection sez = plugin.getConfig().getConfigurationSection("arena.elenco");
            if (sez != null) list.addAll(sez.getKeys(false));
        }
        List<String> filtered = new ArrayList<>();
        for (String s : list) if (s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) filtered.add(s);
        return filtered;
    }
}