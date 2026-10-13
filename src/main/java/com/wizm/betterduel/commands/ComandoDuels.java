package com.wizm.betterduel.commands;

import com.wizm.betterduel.BetterDuel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ComandoDuels implements CommandExecutor {

    private final BetterDuel plugin;

    public ComandoDuels(BetterDuel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("solo-giocatori"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("betterduel.use")) {
            p.sendMessage(plugin.msg("no-permesso"));
            return true;
        }
        if (plugin.getDuelManager().inDuello(p)) {
            p.sendMessage(plugin.msg("gia-in-duello"));
            return true;
        }
        plugin.getMenuDuelli().apri(p);
        return true;
    }
}