package up.wargroove.core.character;

public abstract class Character extends Entity implements Cloneable{

    protected String name;
    protected Stats stats;

    /**
     * Constructor for Character
     * @param name character's name
     * @param faction character's faction
     * @param type type of character
     */
    public Character(String name, Entity.Type type, Faction faction) {
        super(type, faction);
        this.name = name;
        stats = new Stats();
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        Character clone = (Character) super.clone();
        clone.name = this.name;
        clone.stats = new Stats(stats);
        return clone;
    }


    /***************** setters and getters *****************/

    public Faction getFaction() {
        return super.getFaction();
    }

    @Override
    public int getCost() {
        return this.stats.cost;
    }

    public void setCost(int cost) {
        this.stats.cost = cost;
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public double getHealth() {
        return this.stats.health;
    }

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        this.stats.health = health;
    }

    public int getRange() {
        return stats.range;
    }

    public void setRange(int range) {
        this.stats.range = range;
    }

    public double getAttack() {
        return this.stats.health;
    }

    public void setAttack(double attack) {
        this.stats.attack = attack;
    }


    /**
     * Internal class for character's stats
     */
    protected static class Stats {

        public double health; //PH of character
        public double attack; //character Attack
        public int range; //Character range
        public int cost; //Character cost

        /**
         * Constructor for character stats
         */
        public Stats() {
            this.health = 0;
            this.attack = 0;
            this.range = 0;
            this.cost = 0;
        }

        /**
         * Constructor for character stats
         */
        public Stats(Stats stats) {
            this.health = stats.health;
            this.attack = stats.attack;
            this.range = stats.range;
            this.cost = stats.cost;
        }
    }

}
