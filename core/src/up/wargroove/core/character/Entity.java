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

        VILLAGER(Component.GROUND, Movement.Type.WALKING, 3),
        COMMANDER(Component.GROUND, Movement.Type.WALKING, 4),
        WAGON(Component.GROUND, Movement.Type.WHEELS, 12),
        SPAREMAN(Component.GROUND, Movement.Type.WALKING, 3),
        DOG(Component.GROUND, Movement.Type.WALKING, 5),
        ARCHER(Component.GROUND, Movement.Type.WALKING, 3),
        CAVALRY(Component.GROUND, Movement.Type.RIDING, 6),
        SOLDIER(Component.GROUND, Movement.Type.WALKING, 4),
        BALLISTA(Component.GROUND, Movement.Type.WHEELS, 6),
        TREBUCHET(Component.GROUND, Movement.Type.WHEELS, 6),
        GIANT(Component.GROUND, Movement.Type.WALKING, 5),
        RIFLEMAN(Component.GROUND, Movement.Type.WALKING, 4),
        THIEF(Component.SEA, Movement.Type.WALKING, 6),

        BALLON(Component.AIR, Movement.Type.FLYING, 6),
        AERONAUT(Component.AIR, Movement.Type.FLYING, 5),
        SKY_RIDER(Component.AIR, Movement.Type.FLYING, 7),
        DRAGON(Component.AIR, Movement.Type.FLYING, 8),

        BRAGE(Component.SEA, Movement.Type.WATER, 10),
        AMPHIBIAN(Component.SEA, Movement.Type.AMPHIBIOUS, 5),
        TURTLE(Component.SEA, Movement.Type.WATER, 12),
        HARPOON_SHIP(Component.SEA, Movement.Type.WATER, 4),
        WARSHIP(Component.SEA, Movement.Type.WATER, 8),

        CRYSTAL(Component.GROUND, Movement.Type.NULL, 0),
        VINE(Component.SEA, Movement.Type.AMPHIBIOUS, 5),
        SPARROW_BOMB(Component.AIR, Movement.Type.HOVER, 5),
        SHADOW_SISTER(Component.GROUND, Movement.Type.WALKING, 4);

        public Movement.Type movement;
        public Component component;
        public int movementCost;

        Type(Component component, Movement.Type movement, int movementCost) {
	        this.movement = movement;
		    this.component = component;
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
