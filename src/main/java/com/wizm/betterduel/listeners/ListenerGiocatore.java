package com.wizm.betterduel.listeners;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Duel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerGiocatore implements Listener {

    private final BetterDuel plugin;

    public ListenerGiocatore(BetterDuel plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player morto = e.getEntity();
        if (!plugin.getDuelManager().inDuello(morto)) return;

        e.setDeathMessage(null);
        e.getDrops().clear();
        e.setDroppedExp(0);
        e.setKeepInventory(true);
        e.setKeepLevel(true);

        Duel duel = plugin.getDuelManager().getDuello(morto);
        if (duel == null) return;
        Player vincitore = duel.getAvversario(morto).getPlayer();
        plugin.getDuelManager().terminaDuello(duel, vincitore, morto);

        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (morto.isOnline()) {
                try { morto.spigot().respawn(); } catch (Exception ex) { }
            }
        }, 1L);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!plugin.getDuelManager().inDuello(p)) return;
        if (p.getHealth() - e.getFinalDamage() <= 0.5) {
            e.setCancelled(false);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (plugin.getQueueManager().inCoda(p)) {
            plugin.getQueueManager().rimuovi(p);
        }
        if (plugin.getDuelManager().inDuello(p)) {
            plugin.getDuelManager().giocatoreUscito(p);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!plugin.getConfig().getBoolean("arena.blocca-rompere-blocchi", true)) return;
        if (plugin.getDuelManager().inDuello(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!plugin.getConfig().getBoolean("arena.blocca-piazzare-blocchi", true)) return;
        if (plugin.getDuelManager().inDuello(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player vittima = (Player) e.getEntity();
        Player attaccante = (Player) e.getDamager();
        if (!plugin.getDuelManager().inDuello(vittima) && !plugin.getDuelManager().inDuello(attaccante)) return;
        if (plugin.getConfig().getBoolean("arena.blocca-danno-durante-countdown", true)) {
            if (plugin.getDuelManager().inDuello(attaccante) && !plugin.getDuelManager().puoFarePvp(attaccante)) {
                e.setCancelled(true);
            }
        }
    }
}