package up.wargroove.core.character;

public abstract class Entity {

    protected Type type;

    protected Movement movement;
    protected int movRange;

    /**
     * constructeur pour Entity
     *
     * @param type Type d'unité de l'entité
     */
    protected Entity(Type type) {

        this.type = type;
        //this.movRange = movRange;
        //this.movement = movement;

    }

    public abstract void initialize();

    public Type getType() {

        return type;

    }

    public Movement getMovement() {

        return this.movement;

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
        /*
        SPAREMAN,
        DOG,
        ARCHER,
        SOLDIER,
        GIANT,
        RIFLEMAN,
        THIEF,

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
    }
}
