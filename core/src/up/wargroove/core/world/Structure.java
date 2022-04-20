package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;

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

    public static Structure loadStructure(DbObject from, Faction faction) {
        if (from == null) {
            return null;
        }
        Type type = Type.valueOf(from.get(Constants.STRUCTURE_TYPE_DB_KEY).get());
        switch (type) {
            case RECRUITMENT:
                return Recruitment.loadRecruitment(from,faction);
            default: return null;
        }
    }

    @Override
    public DbObject toDBO() {
        DbObject res =  super.toDBO();
        res.put(Constants.STRUCTURE_TYPE_DB_KEY,type);
        return res;
    }
}
