package up.wargroove.core.character;

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
     * constructeur pour l'enumération Movement
     * @param id du type de mouvement
     * @param component Type de mouvement
     */
    Movement(int id, Character.Component component) {

        this.id = id;
		this.component = component;
        
	}
}
