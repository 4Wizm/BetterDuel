package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final BetterDuel plugin;

    public ConfigManager(BetterDuel plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration get() {
        return plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public int getRangeKill(int killGiocatore) {
        int sogliaMedio = plugin.getConfig().getInt("matchmaking.soglia-kill-medio", 50);
        int sogliaAlto = plugin.getConfig().getInt("matchmaking.soglia-kill-alto", 200);
        if (killGiocatore >= sogliaAlto) {
            return plugin.getConfig().getInt("matchmaking.range-kill-alto", 25);
        } else if (killGiocatore >= sogliaMedio) {
            return plugin.getConfig().getInt("matchmaking.range-kill-medio", 10);
        }
        return plugin.getConfig().getInt("matchmaking.range-kill-iniziale", 5);
    }
}