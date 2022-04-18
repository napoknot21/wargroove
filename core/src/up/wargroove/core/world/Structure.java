package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;

public abstract class Structure extends Entity { 
   
    static enum Type {

	    RECRUITMENT,

    }

    private Type type;

    protected Structure(Type type, Faction faction) {

	super(Entity.Type.STRUCTURE,faction);
        this.type = type;
    
    } 

}
