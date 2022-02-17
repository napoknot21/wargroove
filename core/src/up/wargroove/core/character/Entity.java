package up.wargroove.core.character;

public abstract class Entity {

    protected String name;
    protected Type type;
    public int movementCost;
    protected Movement.Type movement;

    /**
     * constructeur pour Entity
     * @param name Nom de l'entité
     * @param type Type d'unité de l'entité
     */
    protected Entity(String name, Type type, int movementCost, Movement.Type movement) {

        this.name = name;
        this.type = type;
        this.movementCost = movementCost;
        this.movement = movement;

    }

    public Type getType() {

        return type;

    }

    public Movement.Type getMovement () {

        return this.movement;

    }

    public int getMovementCost () {

        return this.movementCost;

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
