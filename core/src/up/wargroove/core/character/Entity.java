package up.wargroove.core.character;

import up.wargroove.core.world.Structure;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Savable;

public abstract class Entity implements Savable {

    protected Type type;

    protected Movement movement;
    private Faction faction;
    private double health;

    protected int movRange;
    protected boolean isExhausted;

    /**
     * constructeur pour Entity
     *
     * @param type Type d'unité de l'entité
     */
    protected Entity(Type type, Faction faction) {

        this.type = type;
        this.faction = faction;
        //this.movRange = movRange;
        //this.movement = movement;

    }

    private Entity () {
        this(Type.STRUCTURE, Faction.OUTLAWS);
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void initialize() {
        this.health = getHealth();
        isExhausted = false;
    };

    public boolean isExhausted() {

	    return isExhausted;

    }

    public void exhaust() {
        isExhausted = true;
    }

    public void nextTurn() {

        isExhausted = false;

    }

    public Type getType() {

        return type;

    }

    public Movement getMovement() {

        return this.movement;

    }

    public int getRange() {

        return this.movRange;

    }

    public int getCost() {
        return 0;
    }

    public Faction getFaction() {
        return faction;
    }

    public double getHealth() {
        return health;
    }

    public static Entity loadEntity(DbObject from) {
        if (from == null || from.isEmpty()) {
            return null;
        }
        Type type = Type.valueOf(from.get(Constants.ENTITY_TYPE_DB_KEY).get());
        Faction faction = Faction.valueOf(from.get(Constants.ENTITY_FACTION_DB_KEY).get());
        Entity e;
        if(type == Type.STRUCTURE) {
            e = Structure.loadStructure(from, faction);
        } else {
            e = EntityManager.instantiate(type, "", faction);
        }
        if (e == null) {
            return null;
        }
        e.setHealth(Double.parseDouble(from.get(Constants.ENTITY_HEALTH_DB_KEY).get()));
        return e;
    }

    @Override
    public DbObject toDBO() {
        DbObject res = new DbObject();
        res.put(Constants.ENTITY_HEALTH_DB_KEY, health);
        res.put(Constants.ENTITY_FACTION_DB_KEY, faction.toString());
        res.put(Constants.ENTITY_TYPE_DB_KEY,type.toString());
        return res;
    }

    @Override
    public void load(DbObject from) {
    }

    public enum Component {

        GROUND,
        SEA,
        AIR

    }

    public enum Type {

        VILLAGER,
        COMMANDER,
        SPEARMAN,
        SOLDIER,
        MAGE,
        ARCHER,

        GIANT,
        /*
        RIFLEMAN,
        THIEF,
        DOG,

        CAVALRY,
        BALLISTA,
        WAGON,
        TREBUCHET,

        BALLON,
        AERONAUT,
        SKY_RIDER,
        DRAGON,

        HARPOON_SHIP,
        TURTLE,
        WARSHIP,
        AMPHIBIAN,
        BRAGE,

        CRYSTAL,
        VINE,
        SPARROW_BOMB,
        SHADOW_SISTER;
        */

	STRUCTURE
    }
}
