package com.wizm.betterduel.database;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Stats;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final BetterDuel plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(BetterDuel plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            HikariConfig cfg = new HikariConfig();
            String host = plugin.getConfig().getString("database.host", "localhost");
            int porta = plugin.getConfig().getInt("database.porta", 3306);
            String nome = plugin.getConfig().getString("database.nome", "betterduel");
            cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + porta + "/" + nome + "?useSSL=false&autoReconnect=true");
            cfg.setUsername(plugin.getConfig().getString("database.utente", "root"));
            cfg.setPassword(plugin.getConfig().getString("database.password", ""));
            cfg.setMaximumPoolSize(plugin.getConfig().getInt("database.pool-size", 10));
            cfg.setPoolName("BetterDuelPool");
            dataSource = new HikariDataSource(cfg);
            creaTabella();
            plugin.getLogger().info("Connesso al database MySQL.");
        } catch (Exception e) {
            plugin.getLogger().severe("Errore connessione database: " + e.getMessage());
        }
    }

    private void creaTabella() {
        String sql = "CREATE TABLE IF NOT EXISTS betterduel_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "nome VARCHAR(16), " +
                "duelli INT DEFAULT 0, " +
                "vittorie INT DEFAULT 0, " +
                "sconfitte INT DEFAULT 0, " +
                "kill INT DEFAULT 0, " +
                "death INT DEFAULT 0)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }

    public boolean isConnesso() {
        return dataSource != null && !dataSource.isClosed();
    }

    public Stats getStats(UUID uuid, String nome) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM betterduel_stats WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Stats(rs.getString("uuid"), rs.getString("nome"),
                        rs.getInt("duelli"), rs.getInt("vittorie"), rs.getInt("sconfitte"),
                        rs.getInt("kill"), rs.getInt("death"));
            } else {
                Stats s = new Stats(uuid.toString(), nome, 0, 0, 0, 0, 0);
                inserisciStats(s);
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Stats(uuid.toString(), nome, 0, 0, 0, 0, 0);
        }
    }

    private void inserisciStats(Stats s) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO betterduel_stats (uuid, nome) VALUES (?,?)")) {
            ps.setString(1, s.getUuid());
            ps.setString(2, s.getNome());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void aggiornaStats(Stats s) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE betterduel_stats SET nome=?, duelli=?, vittorie=?, sconfitte=?, kill=?, death=? WHERE uuid=?")) {
            ps.setString(1, s.getNome());
            ps.setInt(2, s.getDuelli());
            ps.setInt(3, s.getVittorie());
            ps.setInt(4, s.getSconfitte());
            ps.setInt(5, s.getKill());
            ps.setInt(6, s.getDeath());
            ps.setString(7, s.getUuid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Stats> getTop(String colonna, int limite) {
        List<Stats> list = new ArrayList<>();
        String sql = "SELECT * FROM betterduel_stats ORDER BY " + colonna + " DESC LIMIT ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Stats(rs.getString("uuid"), rs.getString("nome"),
                        rs.getInt("duelli"), rs.getInt("vittorie"), rs.getInt("sconfitte"),
                        rs.getInt("kill"), rs.getInt("death")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}