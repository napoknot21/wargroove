package up.wargroove.core.character;

public abstract class Entity {

    protected Type type;

    protected Movement movement;

    protected int movRange;
    protected int remainingMovs;
    protected Faction faction;

    /**
     * constructeur pour Entity
     *
     * @param type Type d'unité de l'entité
     */
    protected Entity(Type type, Faction faction) {

        this.type = type;
        //this.movRange = movRange;
        //this.movement = movement;
        this.faction = faction;

    }

    public abstract void initialize();

    public boolean exhausted() {

	    return remainingMovs == 0;

    }

    public void nextTurn() {

	    remainingMovs = movRange + 1;

    }

    public Type getType() {

        return type;

    }

    public Movement getMovement() {

        return this.movement;

    }

    public Faction getFaction () {

        return this.faction;

    }

    public int getRange() {

        return this.movRange;

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
