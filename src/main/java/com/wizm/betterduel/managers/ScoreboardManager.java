package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final BetterDuel plugin;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    public ScoreboardManager(BetterDuel plugin) {
        this.plugin = plugin;
    }

    public void crea(Player p, Player avversario, Kit kit) {
        if (!plugin.getConfig().getBoolean("scoreboard.abilitata", true)) return;
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        String titolo = BetterDuel.color(plugin.getConfig().getString("scoreboard.titolo", "&6BETTERDUEL"));
        Objective obj = board.registerNewObjective("betterduel", "dummy", titolo);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> righe = plugin.getConfig().getStringList("scoreboard.righe");
        int score = righe.size();
        for (int i = 0; i < righe.size(); i++) {
            String line = righe.get(i);
            if (line.isEmpty()) line = " ";
            String entry = org.bukkit.ChatColor.values()[i % 16].toString() + org.bukkit.ChatColor.RESET;
            Team team = board.registerNewTeam("line" + i);
            team.addEntry(entry);
            String processed = BetterDuel.color(line
                    .replace("%avversario%", avversario.getName())
                    .replace("%tuoi_hp%", String.format("%.1f", p.getHealth()))
                    .replace("%suoi_hp%", String.format("%.1f", avversario.getHealth()))
                    .replace("%kit%", kit.getNome()));
            if (processed.length() > 16) {
                team.setPrefix(processed.substring(0, 16));
                if (processed.length() > 32) {
                    team.setSuffix(processed.substring(16, 32));
                } else {
                    team.setSuffix(processed.substring(16));
                }
            } else {
                team.setPrefix(processed);
            }
            obj.getScore(entry).setScore(score--);
        }
        p.setScoreboard(board);
        boards.put(p.getUniqueId(), board);
    }

    public void rimuovi(Player p) {
        if (p == null) return;
        boards.remove(p.getUniqueId());
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}