package up.wargroove.core.character;

public abstract class Entity {

    protected Type type;

    protected Movement movement;
    private Faction faction;
    private double health;

    protected int movRange;
    protected boolean isExhausted;

    /**
     * constructeur pour Entity
     *
     * @param type Type d'unité de l'entité
     */
    protected Entity(Type type, Faction faction) {

        this.type = type;
        this.faction = faction;
        //this.movRange = movRange;
        //this.movement = movement;

    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void initialize() {
        this.health = getHealth();
        isExhausted = false;
    };

    public boolean isExhausted() {

	    return isExhausted;

    }

    public void exhaust() {
        isExhausted = true;
    }

    public void nextTurn() {

        isExhausted = false;

    }

    public Type getType() {

        return type;

    }

    public Movement getMovement() {

        return this.movement;

    }

    public int getRange() {

        return this.movRange;

    }

    public int getCost() {
        return 0;
    }

    public Faction getFaction() {
        return faction;
    }

    public double getHealth() {
        return health;
    }

    public enum Component {

        GROUND,
        SEA,
        AIR

    }

    public enum Type {

        VILLAGER,
        COMMANDER,
        SPEARMAN,
        SOLDIER,
        MAGE,
        ARCHER,

        GIANT,
        /*
        RIFLEMAN,
        THIEF,
        DOG,

        CAVALRY,
        BALLISTA,
        WAGON,
        TREBUCHET,

        BALLON,
        AERONAUT,
        SKY_RIDER,
        DRAGON,

        HARPOON_SHIP,
        TURTLE,
        WARSHIP,
        AMPHIBIAN,
        BRAGE,

        CRYSTAL,
        VINE,
        SPARROW_BOMB,
        SHADOW_SISTER;
        */

	STRUCTURE
    }
}
