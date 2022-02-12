package up.wargroove.core.character;

public class Movement { 

    public enum Type {

        NULL		(-1, Character.Component.GROUND),
        WALKING		(6, Character.Component.GROUND),
        RIDING		(5, Character.Component.GROUND),
        WHEELS		(4, Character.Component.GROUND),
        FLYING		(3, Character.Component.AIR),
        HOVER		(2, Character.Component.AIR),
        WATER		(1, Character.Component.SEA),
        AMPHIBIOUS	(0, Character.Component.SEA);

        public int id;
        public Character.Component component;

        Type(int id, Character.Component component) {
            this.id = id;
            this.component = component;
        }

    };
    private Type moveType;

    public Movement(Type moveType) { 
    
	    this.moveType = moveType;
    
    }

    public Type getMoveType() {
        return moveType;
    }
}
