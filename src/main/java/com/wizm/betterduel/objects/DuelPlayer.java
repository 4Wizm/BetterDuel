package com.wizm.betterduel.objects;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;

public class DuelPlayer {

    private final Player player;
    private final ItemStack[] contenuto;
    private final ItemStack[] armature;
    private final ItemStack itemOffhand;
    private final Location posizione;
    private final World mondoOrigine;
    private final GameMode gameMode;
    private final double salute;
    private final int cibo;
    private final float exp;
    private final int livello;
    private final Collection<PotionEffect> effetti;

    public DuelPlayer(Player p) {
        this.player = p;
        this.contenuto = p.getInventory().getContents().clone();
        this.armature = p.getInventory().getArmorContents().clone();
        this.itemOffhand = p.getInventory().getItemInOffHand().clone();
        this.posizione = p.getLocation().clone();
        this.mondoOrigine = p.getWorld();
        this.gameMode = p.getGameMode();
        this.salute = p.getHealth();
        this.cibo = p.getFoodLevel();
        this.exp = p.getExp();
        this.livello = p.getLevel();
        this.effetti = new ArrayList<>(p.getActivePotionEffects());
    }

    public void ripristina() {
        if (player == null || !player.isOnline()) return;

        player.setFireTicks(0);

        for (PotionEffect e : player.getActivePotionEffects()) player.removePotionEffect(e.getType());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);

        player.getInventory().setContents(contenuto);
        player.getInventory().setArmorContents(armature);
        player.getInventory().setItemInOffHand(itemOffhand);

        player.setGameMode(gameMode);
        player.setHealth(Math.min(salute, player.getMaxHealth()));
        player.setFoodLevel(cibo);
        player.setExp(exp);
        player.setLevel(livello);

        for (PotionEffect e : effetti) player.addPotionEffect(e);

        World mondo = mondoOrigine;
        if (mondo == null) mondo = posizione.getWorld();

        Location destinazione = new Location(mondo,
                posizione.getX(), posizione.getY(), posizione.getZ(),
                posizione.getYaw(), posizione.getPitch());

        player.teleport(destinazione);
        player.updateInventory();
    }

    public Player getPlayer() { return player; }
    public World getMondoOrigine() { return mondoOrigine; }
    public Location getPosizione() { return posizione; }
}