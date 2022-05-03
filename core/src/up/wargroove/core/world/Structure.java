package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;
import up.wargroove.core.character.Movement;

public abstract class Structure extends Entity {


    static enum Type {

	    RECRUITMENT,STRONGHOLD, VILLAGE

    }

    private Type type;

    protected Structure(Type type, Faction faction) {
	    super(Entity.Type.STRUCTURE, faction);
        //super.movement = Movement.NULL;
        this.type = type;
    
    }

    public int getBonus() {
        return 100;
    }

    public static Structure loadStructure(DbObject from, Faction faction) {
        if (from == null) {
            return null;
        }
        Type type = Type.valueOf(from.get(Constants.STRUCTURE_TYPE_DB_KEY).get());
        switch (type) {
            case RECRUITMENT:
                return Recruitment.loadRecruitment(from,faction);
            case VILLAGE:
                return new Village(faction);
            case STRONGHOLD:
                return new Stronghold(faction);
            default: return null;
        }
    }

    @Override
    public DbObject toDBO() {
        DbObject res =  super.toDBO();
        res.put(Constants.STRUCTURE_TYPE_DB_KEY,type);
        return res;
    }

    public Type getStructureType() {
        return type;
    }
}
