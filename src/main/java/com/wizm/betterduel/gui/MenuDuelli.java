package com.wizm.betterduel.gui;

import com.wizm.betterduel.BetterDuel;
import com.wizm.betterduel.objects.Stats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuDuelli {

    private final BetterDuel plugin;

    public MenuDuelli(BetterDuel plugin) {
        this.plugin = plugin;
    }

    public void apri(Player p) {
        String titolo = BetterDuel.color(plugin.getConfig().getString("gui.titolo-menu", "&8&lMenu Duelli"));
        int righe = plugin.getConfig().getInt("gui.righe", 3);
        Inventory inv = Bukkit.createInventory(null, righe * 9, titolo);

        List<UUID> inCoda = plugin.getQueueManager().getGiocatoriInCoda();

        int slot = 0;
        boolean trovatoQualcuno = false;
        for (UUID id : inCoda) {
            if (id.equals(p.getUniqueId())) continue;
            Player target = Bukkit.getPlayer(id);
            if (target == null || !target.isOnline()) continue;
            trovatoQualcuno = true;

            ItemStack testa = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) testa.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(target);
                String nome = plugin.getConfig().getString("gui.giocatore.nome", "&a%player%")
                        .replace("%player%", target.getName());
                meta.setDisplayName(BetterDuel.color(nome));

                String kitId = plugin.getQueueManager().getKit(target);
                Stats s = plugin.getStatsManager().getStats(target);

                List<String> lore = new ArrayList<>();
                for (String l : plugin.getConfig().getStringList("gui.giocatore.lore")) {
                    lore.add(BetterDuel.color(l
                            .replace("%player%", target.getName())
                            .replace("%kit%", kitId != null ? kitId : "sconosciuto")
                            .replace("%kill%", String.valueOf(s.getKill()))
                            .replace("%vittorie%", String.valueOf(s.getVittorie()))
                            .replace("%sconfitte%", String.valueOf(s.getSconfitte()))
                            .replace("%duelli%", String.valueOf(s.getDuelli()))));
                }
                meta.setLore(lore);
                testa.setItemMeta(meta);
            }
            inv.setItem(slot, testa);
            slot++;
            if (slot >= righe * 9) break;
        }

        if (!trovatoQualcuno) {
            String matStr = plugin.getConfig().getString("gui.nessun-giocatore.materiale", "BARRIER");
            Material mat = Material.matchMaterial(matStr);
            if (mat == null) mat = Material.BARRIER;
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String nome = plugin.getConfig().getString("gui.nessun-giocatore.nome", "&cNessun giocatore in coda");
                meta.setDisplayName(BetterDuel.color(nome));
                String primoKit = plugin.getKitManager().getKits().keySet().stream().findFirst().orElse("soldato");
                List<String> lore = new ArrayList<>();
                for (String l : plugin.getConfig().getStringList("gui.nessun-giocatore.lore")) {
                    lore.add(BetterDuel.color(l.replace("%kit%", primoKit)));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(13, item);
        }

        p.openInventory(inv);
    }
}