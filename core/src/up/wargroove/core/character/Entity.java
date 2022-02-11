package up.wargroove.core.character;

import up.wargroove.core.world.Tile;

public abstract class Entity {

    protected String name;

    public static enum Component {
        	
	    GROUND,
	    SEA,
	    AIR;
    
    }

    public static enum Type {

        VILLAGER(Movement.Type.WALKING, 3),
        COMMANDER(Movement.Type.WALKING, 4),
        WAGON(Movement.Type.WHEELS, 12),
        SPAREMAN(Movement.Type.WALKING, 3),
        DOG(Movement.Type.WALKING, 5),
        ARCHER(Movement.Type.WALKING, 3),
        CAVALRY(Movement.Type.RIDING, 6),
        SOLDIER(Movement.Type.WALKING, 4),
        BALLISTA(Movement.Type.WHEELS, 6),
        TREBUCHET(Movement.Type.WHEELS, 6),
        GIANT(Movement.Type.WALKING, 5),
        RIFLEMAN(Movement.Type.WALKING, 4),
        THIEF(Movement.Type.WALKING, 6),

        BALLON(Movement.Type.FLYING, 6),
        AERONAUT(Movement.Type.FLYING, 5),
        SKY_RIDER(Movement.Type.FLYING, 7),
        DRAGON(Movement.Type.FLYING, 8),

        BRAGE(Movement.Type.WATER, 10),
        AMPHIBIAN(Movement.Type.AMPHIBIOUS, 5),
        TURTLE(Movement.Type.WATER, 12),
        HARPOON_SHIP(Movement.Type.WATER, 4),
        WARSHIP(Movement.Type.WATER, 8),

        CRYSTAL(Movement.Type.NULL, 0),
        VINE(Movement.Type.AMPHIBIOUS, 5),
        SPARROW_BOMB(Movement.Type.HOVER, 5),
        SHADOW_SISTER(Movement.Type.WALKING, 4);

        public Movement.Type movement;
        public int movementCost;

        Type(Movement.Type movement, int movementCost) {
	        this.movement = movement;
            this.movementCost = movementCost;
	    }

    };

    protected Type type;
    public int tmpCost;

    protected Entity (String name, Type type) {

        this.name = name;
    	this.type = type;
        tmpCost = type.movementCost;

    }

    public Type getType() {

	    return type;

    }
}
