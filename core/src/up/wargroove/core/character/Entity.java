package up.wargroove.core.character;

public abstract class Entity {

    public int tmpCost;
    protected String name;
    protected Type type;

    /**
     * constructeur pour Entity
     * @param name Nom de l'entité
     * @param type Type d'unité de l'entité
     */
    protected Entity(String name, Type type) {

        this.name = name;
        this.type = type;
        tmpCost = type.movementCost;

    }

    public Type getType() {

        return type;

    }

    public enum Component {

        GROUND,
        SEA,
        AIR

    }

    public enum Type {

        VILLAGER(Movement.Type.WALKING, 3),
        COMMANDER(Movement.Type.WALKING, 4),
        SPAREMAN(Movement.Type.WALKING, 3),
        DOG(Movement.Type.WALKING, 5),
        ARCHER(Movement.Type.WALKING, 3),
        SOLDIER(Movement.Type.WALKING, 4),
        GIANT(Movement.Type.WALKING, 5),
        RIFLEMAN(Movement.Type.WALKING, 4),
        THIEF(Movement.Type.WALKING, 6),

        CAVALRY(Movement.Type.RIDING, 6),
        BALLISTA(Movement.Type.WHEELS, 6),
        WAGON(Movement.Type.WHEELS, 12),
        TREBUCHET(Movement.Type.WHEELS, 6),

        BALLON(Movement.Type.FLYING, 6),
        AERONAUT(Movement.Type.FLYING, 5),
        SKY_RIDER(Movement.Type.FLYING, 7),
        DRAGON(Movement.Type.FLYING, 8),

        HARPOON_SHIP(Movement.Type.WATER, 4),
        TURTLE(Movement.Type.WATER, 12),
        WARSHIP(Movement.Type.WATER, 8),
        AMPHIBIAN(Movement.Type.AMPHIBIOUS, 5),
        BRAGE(Movement.Type.WATER, 10),

        CRYSTAL(Movement.Type.NULL, 0),
        VINE(Movement.Type.AMPHIBIOUS, 5),
        SPARROW_BOMB(Movement.Type.HOVER, 5),
        SHADOW_SISTER(Movement.Type.WALKING, 4);

        public Movement.Type movement;
        public int movementCost;

        /**
         * constructeur pour Type
         * @param movement     Type de mouvement de l'entité
         * @param movementCost Coût de movement de l'entité
         */
        Type(Movement.Type movement, int movementCost) {
            this.movement = movement;
            this.movementCost = movementCost;
        }

    }
}
