package up.wargroove.core.character;

public abstract class Character extends Entity {

    protected String name;

    private Faction faction;
    protected Stats stats; 

    /**
     * Constructeur pour Character
     * @param name nom du personnage
     * @param faction Faction du personnage
     * @param type Type d'unité du personnage
     */
    public Character(String name, Entity.Type type, Faction faction) {

        super(type);

	this.name = name;
        this.faction = faction;

        stats = new Stats();

    }

    /**
     * getter et setters pour la faction
     * @return faction du personnage
     */
    public Faction getFaction() {
        return faction;
    }

    public int getCost() {
        return this.stats.cost;
    }
    // FIXME: 01/04/2022 Cet override cause le bug
    /*public int getRange() {
        return this.stats.range;
    }*/

    public Stats getStats() {
        return stats;
    }

    public double getHealth() {
        return this.stats.health;
    }

    public double getAttack() {
        return this.stats.health;
    }

    public double getDefense() {
        return this.stats.defense;
    }

    public void setHealth(double health) {
        this.stats.health = health;
    }

    public void setAttack(double attack) {
        this.stats.attack = attack;
    }

    public void setDefense(double defense) {
        this.stats.defense = defense;
    }

    public void setCapture(boolean capture) {
        this.stats.capture = capture;
    }

    public void setSight (int sight) {
        this.stats.sight = sight;
    }

    public void setRange (int range) {
        this.stats.range = range;
    }

    public void setCost (int cost) {
        this.stats.cost = cost;
    }

    protected static class Stats {

        public double health; //up to 100
        public double attack;
        public double defense; //pourcentage 0 to 100
        public boolean capture; // true = yes, false = no
        public int sight;
        public int range;
        public int cost;

        public Stats() {
            this.health = 0;
            this.attack = 0;
            this.defense = 0;
            this.capture = false;
            this.sight = 0;
            this.range = 0;
            this.cost = 0;
        }

        /**
         * Constructeur pour les stats d'un personnage
         * //@param health Total de vie
         * //@param attack Attaque du personnage
         * //@param cost Prix du personnage (monnaie du jeu)
         * //@param range Rang d'attaque du personnage
         * //@param defense Pourcentage qui réduit l'attaque du personnage attaquant
         * //@param sight Vue du personnage
         * //@param capture Capacité à capture un village
         */


        /*
        public Stats (double health, double attack, double defense, int sight, int cost, Movement movement, int range) {
            this.health = health;
            this.attack = attack;
            this.defense = defense;
            this.sight = sight;
            this.cost = cost;
            this.range = range;
            this.movement = movement;
        }
        */

    }

}
