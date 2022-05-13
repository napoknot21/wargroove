package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;
import up.wargroove.core.character.Movement;

public abstract class Structure extends Entity {

    /**
     * Enum for structure types
     */
    static enum Type {

        RECRUITMENT, STRONGHOLD, VILLAGE

    }

    private Type type;

    /**
     * Constructor for Structure
     * @param type structure type
     * @param faction structure faction
     */
    protected Structure(Type type, Faction faction) {
        super(Entity.Type.STRUCTURE, faction);
        movement = Movement.NULL;
        this.type = type;
        setHealth((faction == Faction.OUTLAWS)? 1: 100);
    }


    /**
     * Load a structure from a database
     * @param from database
     * @param faction structure faction
     * @return a new structure object, null else
     */
    public static Structure loadStructure(DbObject from, Faction faction) {
        if (from == null) {
            return null;
        }
        Type type = Type.valueOf(from.get(Constants.STRUCTURE_TYPE_DB_KEY).get());
        switch (type) {
            case RECRUITMENT:
                return Recruitment.loadRecruitment(from, faction);
            case VILLAGE:
                return new Village(faction);
            case STRONGHOLD:
                return new Stronghold(faction);
            default:
                return null;
        }
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public DbObject toDBO() {
        DbObject res = super.toDBO();
        res.put(Constants.STRUCTURE_TYPE_DB_KEY, type);
        return res;
    }

    @Override
    public void nextTurn() {
        isExhausted = false;
        if (this.getHealth() < 100) {
            this.setHealth(getHealth() + 10);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Structure clone = (Structure) super.clone();
        clone.type = this.type;
        return clone;
    }

    /***************** setters and getters *****************/

    public Type getStructureType() {
        return type;
    }

    public int getBonus() {
        return 100;
    }

}