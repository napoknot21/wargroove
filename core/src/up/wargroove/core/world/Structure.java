package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;

public abstract class Structure extends Entity {
    private final int bonus;
   
    static enum Type {

	    RECRUITMENT,

    }

    private Type type;

    protected Structure(Type type, Faction faction, int bonus) {
	super(Entity.Type.STRUCTURE, faction);
        this.type = type;
        this.bonus = bonus;
    
    }

    public int getBonus() {
        return bonus;
    }
}
