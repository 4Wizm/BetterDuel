package com.wizm.betterduel.listeners;

import com.wizm.betterduel.BetterDuel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ListenerMenu implements Listener {

    private final BetterDuel plugin;

    public ListenerMenu(BetterDuel plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        String titolo = BetterDuel.color(plugin.getConfig().getString("gui.titolo-menu", "&8&lMenu Duelli"));
        if (!e.getView().getTitle().equals(titolo)) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            if (meta == null || meta.getOwningPlayer() == null) return;
            Player target = meta.getOwningPlayer().getPlayer();
            if (target == null || !target.isOnline()) {
                p.sendMessage(BetterDuel.color("&cGiocatore non piu online!"));
                p.closeInventory();
                return;
            }
            String kitId = plugin.getQueueManager().getKit(target);
            if (kitId == null) {
                p.sendMessage(BetterDuel.color("&cQuesto giocatore non e piu in coda!"));
                p.closeInventory();
                return;
            }
            p.closeInventory();
            plugin.getQueueManager().aggiungi(p, kitId);
        }
    }
}