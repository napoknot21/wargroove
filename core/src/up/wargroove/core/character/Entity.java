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

        VILLAGER(Component.GROUND),
        COMMANDER(Component.GROUND),
        WAGON(Component.GROUND),
        SPAREMAN(Component.GROUND),
        DOG(Component.GROUND),
        ARCHER(Component.GROUND),
        CAVALRY(Component.GROUND),
        SOLDIER(Component.GROUND),
        BALLISTA(Component.GROUND),
        TREBUCHET(Component.GROUND),
        GIANT(Component.GROUND),
        RIFLEMAN(Component.GROUND),
        THIEF(Component.SEA),

        BALLON(Component.AIR),
        AERONAUT(Component.AIR),
        SKY_RIDER(Component.AIR),
        DRAGON(Component.AIR),

        BRAGE(Component.SEA),
        AMPHIBIAN(Component.SEA),
        TURTLE(Component.SEA),
        HARPOON_SHIP(Component.SEA),
        WARSHIP(Component.SEA),

        CRYSTAL(Component.GROUND),
        VINE(Component.SEA),
        SPARROW_BOMB(Component.AIR),
        SHADOW_SISTER(Component.GROUND);

	public Movement.Type movement;
	public Component component;
	public int cost = 5;

        Type(Component component) {
	
		this.component = component;

	}

    };

    protected Type type;
    public int tmpCost;

    protected Entity (String name, Type type) {

        this.name = name;
    	this.type = type;

	tmpCost = type.cost;

    }

    public Type getType() {

	    return type;

    }
}
