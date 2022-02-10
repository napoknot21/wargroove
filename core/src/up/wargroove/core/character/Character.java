package up.wargroove.core.character;

import up.wargroove.core.world.Tile;

public class Character extends Entity { 

    private Faction faction;

    private int cost;
    private int range;
    private boolean capture;
    /*
        true = yes
        false = no
    */
    private Stats stats; 

    public Character(String name, Faction faction, Type type, int cost, int range, boolean capture, Stats stats) {
        super(name, type);

        this.faction = faction; 
        this.cost = cost;
        this.range = range;
        this.capture = capture;
        this.stats = stats;

    }

    public Faction getFaction() {
        return faction;
    }

    public int getCost() {
        return cost;
    }

    public int getRange() {
        return range;
    }

    public boolean isCapture() {
        return capture;
    }

    public Stats getStats() {
        return stats;
    }

    /*
     * Quelques fonctions de bases pour les personnages
     */

    public boolean isAlive () {
        return (stats.getHealth() > 0);
    }

    public boolean attack (Character ch) {
        if (!this.isAlive() || !ch.isAlive()) return false;
        ch.stats.setHealth(ch.stats.getHealth()-(ch.stats.getDefense()*this.stats.getAttack()/100));
        return true;
    }
}
