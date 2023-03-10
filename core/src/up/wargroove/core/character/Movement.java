package up.wargroove.core.character;

/**
 * Types of movements for characters
 */
public enum Movement {

    NULL		(-1, Character.Component.GROUND),
    WALKING		(6, Character.Component.GROUND),
    RIDING		(5, Character.Component.GROUND),
    WHEELS		(4, Character.Component.GROUND),
    FLYING		(3, Character.Component.AIR),
    HOVER		(2, Character.Component.AIR),
    WATER		(1, Character.Component.SEA),
    AMPHIBIOUS	(0, Character.Component.SEA);

    public int id;
    public Entity.Component component;

    /**
     * constructor for enum movement
     * @param id movement type
     * @param component terrain type
     */
    Movement(int id, Character.Component component) {
        this.id = id;
		this.component = component;
	}
}
