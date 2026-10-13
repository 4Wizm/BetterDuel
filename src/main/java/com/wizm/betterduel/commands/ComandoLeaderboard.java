package com.wizm.betterduel.commands;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Stats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComandoLeaderboard implements CommandExecutor, TabCompleter {

    private final BetterDuel plugin;

    public ComandoLeaderboard(BetterDuel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String tipo = "vittorie";
        if (args.length >= 1) {
            String t = args[0].toLowerCase();
            if (t.equals("duelli") || t.equals("vittorie") || t.equals("sconfitte") || t.equals("kill")) tipo = t;
        }

        if (!plugin.getDatabaseManager().isConnesso()) {
            sender.sendMessage(plugin.msg("database-errore"));
            return true;
        }

        List<Stats> top = plugin.getDatabaseManager().getTop(tipo, 10);
        sender.sendMessage(BetterDuel.color(plugin.getConfig().getString("messaggi.leaderboard-titolo", "&6=== Classifica ===")));
        sender.sendMessage(BetterDuel.color("&7Categoria: &e" + tipo));
        if (top.isEmpty()) {
            sender.sendMessage(plugin.msg("leaderboard-vuota"));
            return true;
        }
        int pos = 1;
        for (Stats s : top) {
            int valore;
            if (tipo.equals("duelli")) valore = s.getDuelli();
            else if (tipo.equals("sconfitte")) valore = s.getSconfitte();
            else if (tipo.equals("kill")) valore = s.getKill();
            else valore = s.getVittorie();
            sender.sendMessage(BetterDuel.color("&e" + pos + ". &f" + s.getNome() + " &7- &a" + valore));
            pos++;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.addAll(Arrays.asList("duelli", "vittorie", "sconfitte", "kill"));
        }
        return list;
    }
}