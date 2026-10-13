package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Stats;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {

    private final BetterDuel plugin;
    private final Map<UUID, Stats> cache = new HashMap<>();

    public StatsManager(BetterDuel plugin) {
        this.plugin = plugin;
    }

    public Stats getStats(Player p) {
        UUID id = p.getUniqueId();
        if (cache.containsKey(id)) return cache.get(id);
        Stats s;
        if (plugin.getDatabaseManager().isConnesso()) {
            s = plugin.getDatabaseManager().getStats(id, p.getName());
        } else {
            s = new Stats(id.toString(), p.getName(), 0, 0, 0, 0, 0);
        }
        cache.put(id, s);
        return s;
    }

    public void salva(Stats s) {
        if (plugin.getDatabaseManager().isConnesso()) {
            plugin.getDatabaseManager().aggiornaStats(s);
        }
    }

    public void aggiungiVittoria(Player p) {
        Stats s = getStats(p);
        s.setVittorie(s.getVittorie() + 1);
        s.setDuelli(s.getDuelli() + 1);
        salva(s);
    }

    public void aggiungiSconfitta(Player p) {
        Stats s = getStats(p);
        s.setSconfitte(s.getSconfitte() + 1);
        s.setDuelli(s.getDuelli() + 1);
        s.setDeath(s.getDeath() + 1);
        salva(s);
    }

    public void aggiungiKill(Player p) {
        Stats s = getStats(p);
        s.setKill(s.getKill() + 1);
        salva(s);
    }

    public void rimuoviCache(UUID id) {
        cache.remove(id);
    }
}