package com.wizm.betterduel;

import com.wizm.betterduel.commands.ComandoDuel;
import com.wizm.betterduel.commands.ComandoDuels;
import com.wizm.betterduel.commands.ComandoLeaderboard;
import com.wizm.betterduel.database.DatabaseManager;
import com.wizm.betterduel.gui.MenuDuelli;
import com.wizm.betterduel.listeners.ListenerGiocatore;
import com.wizm.betterduel.listeners.ListenerMenu;
import com.wizm.betterduel.managers.ConfigManager;
import com.wizm.betterduel.managers.DuelManager;
import com.wizm.betterduel.managers.KitManager;
import com.wizm.betterduel.managers.QueueManager;
import com.wizm.betterduel.managers.ScoreboardManager;
import com.wizm.betterduel.managers.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterDuel extends JavaPlugin {

    private static BetterDuel instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private KitManager kitManager;
    private DuelManager duelManager;
    private QueueManager queueManager;
    private StatsManager statsManager;
    private ScoreboardManager scoreboardManager;
    private MenuDuelli menuDuelli;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.kitManager = new KitManager(this);
        this.statsManager = new StatsManager(this);
        this.databaseManager = new DatabaseManager(this);

        if (getConfig().getBoolean("database.abilitato", false)) {
            databaseManager.connect();
        }

        this.duelManager = new DuelManager(this);
        this.queueManager = new QueueManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.menuDuelli = new MenuDuelli(this);

        if (getConfig().getBoolean("impostazioni.crea-mondo-se-assente", true)) {
            String mondo = getConfig().getString("impostazioni.mondo-duelli", "betterduel_arena");
            World w = Bukkit.getWorld(mondo);
            if (w == null) {
                WorldCreator creator = new WorldCreator(mondo);
                creator.environment(World.Environment.NORMAL);
                creator.generateStructures(false);
                Bukkit.createWorld(creator);
                getLogger().info("Mondo duelli creato: " + mondo);
            }
        }

        getCommand("duel").setExecutor(new ComandoDuel(this));
        getCommand("duels").setExecutor(new ComandoDuels(this));
        getCommand("duelleaderboard").setExecutor(new ComandoLeaderboard(this));
        Bukkit.getPluginManager().registerEvents(new ListenerGiocatore(this), this);
        Bukkit.getPluginManager().registerEvents(new ListenerMenu(this), this);

        getLogger().info("BetterDuel v1.0.1 abilitato!");
    }

    @Override
    public void onDisable() {
        if (duelManager != null) duelManager.terminaTuttiIDuelli();
        if (databaseManager != null) databaseManager.disconnect();
        getLogger().info("BetterDuel disabilitato.");
    }

    public static BetterDuel getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public KitManager getKitManager() { return kitManager; }
    public DuelManager getDuelManager() { return duelManager; }
    public QueueManager getQueueManager() { return queueManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public MenuDuelli getMenuDuelli() { return menuDuelli; }

    public String msg(String path) {
        String prefix = getConfig().getString("messaggi.prefisso", "");
        String m = getConfig().getString("messaggi." + path, path);
        return color(prefix + m);
    }

    public static String color(String s) {
        if (s == null) return "";
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }
}