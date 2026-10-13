package com.wizm.betterduel.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Duel {

    private final UUID id;
    private final DuelPlayer p1;
    private final DuelPlayer p2;
    private final Kit kit;
    private final Location arena1;
    private final Location arena2;
    private final Arena arena;
    private final long inizio;
    private boolean terminato;
    private boolean pvpAttivo;

    public Duel(DuelPlayer p1, DuelPlayer p2, Kit kit, Location a1, Location a2, Arena arena) {
        this.id = UUID.randomUUID();
        this.p1 = p1;
        this.p2 = p2;
        this.kit = kit;
        this.arena1 = a1;
        this.arena2 = a2;
        this.arena = arena;
        this.inizio = System.currentTimeMillis();
        this.terminato = false;
        this.pvpAttivo = false;
    }

    public UUID getId() { return id; }
    public DuelPlayer getP1() { return p1; }
    public DuelPlayer getP2() { return p2; }
    public Kit getKit() { return kit; }
    public Location getArena1() { return arena1; }
    public Location getArena2() { return arena2; }
    public Arena getArena() { return arena; }
    public long getInizio() { return inizio; }
    public boolean isTerminato() { return terminato; }
    public void setTerminato(boolean b) { this.terminato = b; }
    public boolean isPvpAttivo() { return pvpAttivo; }
    public void setPvpAttivo(boolean b) { this.pvpAttivo = b; }

    public DuelPlayer getAvversario(Player p) {
        if (p1.getPlayer().equals(p)) return p2;
        if (p2.getPlayer().equals(p)) return p1;
        return null;
    }

    public boolean contiene(Player p) {
        return p1.getPlayer().equals(p) || p2.getPlayer().equals(p);
    }

    public DuelPlayer getDuelPlayer(Player p) {
        if (p1.getPlayer().equals(p)) return p1;
        if (p2.getPlayer().equals(p)) return p2;
        return null;
    }
}