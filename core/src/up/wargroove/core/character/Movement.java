package up.wargroove.core.character;

public class Movement { 

    public enum Type {

        NULL		(-1, Character.Component.GROUND),
        WALKING		(0, Character.Component.GROUND),
        RIDING		(1, Character.Component.GROUND),
        WHEELS		(2, Character.Component.GROUND),
        FLYING		(3, Character.Component.AIR),
        HOVER		(4, Character.Component.AIR),
        WATER		(5, Character.Component.SEA),
        AMPHIBIOUS	(6, Character.Component.SEA);

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
