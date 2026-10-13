package com.wizm.betterduel.managers;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Kit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    private final BetterDuel plugin;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(BetterDuel plugin) {
        this.plugin = plugin;
        caricaKits();
    }

    public void caricaKits() {
        kits.clear();
        ConfigurationSection sez = plugin.getConfig().getConfigurationSection("kit");
        if (sez == null) return;
        for (String key : sez.getKeys(false)) {
            String nome = sez.getString(key + ".nome", key);
            String permesso = sez.getString(key + ".permesso", "betterduel.kit." + key);
            String iconaStr = sez.getString(key + ".icona", "STONE");
            Material icona = Material.matchMaterial(iconaStr);
            if (icona == null) icona = Material.STONE;

            List<String> contenutoStr = sez.getStringList(key + ".contenuto");
            List<ItemStack> contenuto = new ArrayList<>();
            for (String s : contenutoStr) {
                ItemStack item = parseItem(s);
                if (item != null) contenuto.add(item);
            }
            kits.put(key.toLowerCase(), new Kit(key.toLowerCase(), nome, permesso, icona, contenuto));
        }
        plugin.getLogger().info("Caricati " + kits.size() + " kit.");
    }

    private ItemStack parseItem(String s) {
        try {
            String[] split = s.split(":");
            Material mat = Material.matchMaterial(split[0]);
            if (mat == null) return null;
            int qta = split.length > 1 ? Integer.parseInt(split[1]) : 1;
            return new ItemStack(mat, qta);
        } catch (Exception e) {
            return null;
        }
    }

    public Kit getKit(String nome) {
        return kits.get(nome.toLowerCase());
    }

    public Map<String, Kit> getKits() { return kits; }

    public boolean haPermesso(Player p, Kit kit) {
        return p.hasPermission(kit.getPermesso()) || p.hasPermission("betterduel.admin");
    }

    public void daiKit(Player p, Kit kit) {
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setItemInOffHand(null);
        for (ItemStack item : kit.getContenuto()) {
            if (isArmatura(item)) {
                equipaggiaArmatura(p, item);
            } else {
                p.getInventory().addItem(item.clone());
            }
        }
        p.updateInventory();
    }

    private boolean isArmatura(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        String n = item.getType().name();
        return n.endsWith("_HELMET") || n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS")
                || n.endsWith("_BOOTS") || n.equals("SHIELD") || n.equals("TURTLE_HELMET")
                || n.equals("ELYTRA") || n.equals("CARVED_PUMPKIN");
    }

    private void equipaggiaArmatura(Player p, ItemStack item) {
        String n = item.getType().name();
        ItemStack[] arm = p.getInventory().getArmorContents();
        if (n.endsWith("_HELMET") || n.equals("TURTLE_HELMET") || n.equals("CARVED_PUMPKIN")) {
            arm[3] = item.clone();
        } else if (n.endsWith("_CHESTPLATE") || n.equals("ELYTRA")) {
            arm[2] = item.clone();
        } else if (n.endsWith("_LEGGINGS")) {
            arm[1] = item.clone();
        } else if (n.endsWith("_BOOTS")) {
            arm[0] = item.clone();
        } else if (n.equals("SHIELD")) {
            p.getInventory().setItemInOffHand(item.clone());
            return;
        }
        p.getInventory().setArmorContents(arm);
    }
}