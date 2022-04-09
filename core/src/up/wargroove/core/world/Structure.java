package up.wargroove.core.world;

import up.wargroove.core.character.Entity;

public abstract class Structure extends Entity { 
   
    static enum Type {

	    RECRUITMENT,

    }

    private Type type;

    protected Structure(Type type) {

	super(Entity.Type.STRUCTURE);
        this.type = type;
    
    } 

}
