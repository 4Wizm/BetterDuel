package com.wizm.betterduel.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {

    private final String id;
    private final String nome;
    private final String permesso;
    private final Material icona;
    private final List<ItemStack> contenuto;

    public Kit(String id, String nome, String permesso, Material icona, List<ItemStack> contenuto) {
        this.id = id;
        this.nome = nome;
        this.permesso = permesso;
        this.icona = icona;
        this.contenuto = contenuto;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getPermesso() { return permesso; }
    public Material getIcona() { return icona; }
    public List<ItemStack> getContenuto() { return contenuto; }
}