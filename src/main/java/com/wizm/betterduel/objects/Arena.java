package com.wizm.betterduel.objects;

import org.bukkit.Location;

public class Arena {

    private final String nome;
    private final Location spawn1;
    private final Location spawn2;
    private boolean occupata;

    public Arena(String nome, Location spawn1, Location spawn2) {
        this.nome = nome;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.occupata = false;
    }

    public String getNome() { return nome; }
    public Location getSpawn1() { return spawn1; }
    public Location getSpawn2() { return spawn2; }
    public boolean isOccupata() { return occupata; }
    public void setOccupata(boolean b) { this.occupata = b; }
}