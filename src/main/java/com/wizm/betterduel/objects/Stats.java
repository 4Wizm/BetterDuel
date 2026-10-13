package com.wizm.betterduel.objects;

public class Stats {

    private final String uuid;
    private final String nome;
    private int duelli;
    private int vittorie;
    private int sconfitte;
    private int kill;
    private int death;

    public Stats(String uuid, String nome, int duelli, int vittorie, int sconfitte, int kill, int death) {
        this.uuid = uuid;
        this.nome = nome;
        this.duelli = duelli;
        this.vittorie = vittorie;
        this.sconfitte = sconfitte;
        this.kill = kill;
        this.death = death;
    }

    public String getUuid() { return uuid; }
    public String getNome() { return nome; }
    public int getDuelli() { return duelli; }
    public int getVittorie() { return vittorie; }
    public int getSconfitte() { return sconfitte; }
    public int getKill() { return kill; }
    public int getDeath() { return death; }
    public void setDuelli(int v) { this.duelli = v; }
    public void setVittorie(int v) { this.vittorie = v; }
    public void setSconfitte(int v) { this.sconfitte = v; }
    public void setKill(int v) { this.kill = v; }
    public void setDeath(int v) { this.death = v; }

    public double getKdr() {
        if (death == 0) return kill;
        return Math.round((double) kill / death * 100.0) / 100.0;
    }
}