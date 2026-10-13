package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QueueManager {

    private final BetterDuel plugin;
    private final Map<UUID, String> inCoda = new HashMap<>();
    private final Map<UUID, Long> tempoEntrata = new HashMap<>();
    private int taskId = -1;

    public QueueManager(BetterDuel plugin) {
        this.plugin = plugin;
        avviaTask();
    }

    public void avviaTask() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tentaMatchmaking, 40L, 40L);
    }

    public void aggiungi(Player p, String kitId) {
        UUID id = p.getUniqueId();
        if (inCoda.containsKey(id)) {
            p.sendMessage(plugin.msg("gia-in-coda"));
            return;
        }
        if (plugin.getDuelManager().inDuello(p)) {
            p.sendMessage(plugin.msg("gia-in-duello"));
            return;
        }
        inCoda.put(id, kitId);
        tempoEntrata.put(id, System.currentTimeMillis());
        p.sendMessage(plugin.msg("entrato-coda").replace("%kit%", kitId));
    }

    public void rimuovi(Player p) {
        inCoda.remove(p.getUniqueId());
        tempoEntrata.remove(p.getUniqueId());
    }

    public boolean inCoda(Player p) {
        return inCoda.containsKey(p.getUniqueId());
    }

    public String getKit(Player p) {
        return inCoda.get(p.getUniqueId());
    }

    public List<UUID> getGiocatoriInCoda() {
        return new ArrayList<>(inCoda.keySet());
    }

    private void tentaMatchmaking() {
        if (inCoda.size() < 2) return;
        List<UUID> keys = new ArrayList<>(inCoda.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                UUID a = keys.get(i);
                UUID b = keys.get(j);
                if (!inCoda.containsKey(a) || !inCoda.containsKey(b)) continue;
                if (!inCoda.get(a).equals(inCoda.get(b))) continue;

                Player p1 = Bukkit.getPlayer(a);
                Player p2 = Bukkit.getPlayer(b);
                if (p1 == null || p2 == null || !p1.isOnline() || !p2.isOnline()) continue;

                int kill1 = plugin.getStatsManager().getStats(p1).getKill();
                int kill2 = plugin.getStatsManager().getStats(p2).getKill();
                int range1 = plugin.getConfigManager().getRangeKill(kill1);
                int range2 = plugin.getConfigManager().getRangeKill(kill2);
                int differenza = Math.abs(kill1 - kill2);

                long tempoMax = plugin.getConfig().getInt("matchmaking.tempo-allargamento-range", 20) * 1000L;
                boolean tempoScaduto = (System.currentTimeMillis() - tempoEntrata.getOrDefault(a, 0L)) > tempoMax
                        || (System.currentTimeMillis() - tempoEntrata.getOrDefault(b, 0L)) > tempoMax;

                boolean match = differenza <= Math.max(range1, range2);
                if (!match && tempoScaduto) match = true;
                if (!match) continue;

                Kit kit = plugin.getKitManager().getKit(inCoda.get(a));
                if (kit == null) continue;

                rimuovi(p1);
                rimuovi(p2);
                plugin.getDuelManager().avviaDuello(p1, p2, kit);
                return;
            }
        }
    }

    public void clear() {
        inCoda.clear();
        tempoEntrata.clear();
    }
}