package up.wargroove.core.character;

import up.wargroove.core.world.Structure;
import up.wargroove.utils.Constants;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Savable;
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.utils.Constants;
import up.wargroove.utils.Pair;
import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public abstract class Entity implements Savable {

    protected Type type;

    private Random r = new Random();

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
        if (health < 0) this.health = 0;
        else this.health = health;
    }

    public void initialize() {
        this.health = getHealth();
        isExhausted = false;
    }

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

    public int getMovRange() {

        return this.movRange;

    }

    public int getCost() {
        return 0;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
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

    public void attack (Entity ch) {
        if (ch == null) return;
        Pair<Integer,Integer> attacksMedian = this.getAttacksValues(ch);
        int baseDamage = this.getAttacksAndBaseValues(ch).get("base").get(0);
        int a = attacksMedian.first - baseDamage;
        if (a <= 0) {
            ch.setHealth((ch.getHealth() - baseDamage < 0) ? 0 : (ch.getHealth() - baseDamage) );
            return;
        }
        int min = attacksMedian.first - a;
        int max = attacksMedian.first + a;
        int randAttack = r.nextInt(max - min + 1) + min;
        ch.setHealth((ch.getHealth() - randAttack < 0) ? 0 : (ch.getHealth() - randAttack) );

    }

    protected Map<String, Map<String, List<Integer>>> readDamageMatrixValues () {
        try {
            String name = this.getType().toString().toLowerCase();
            File f = new File(Constants.DEFAULT_DM_ROOT + name + ".yml");
            return new Yaml().load(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, List <Integer> > getAttacksAndBaseValues (Entity ch) {
        var data = this.readDamageMatrixValues();
        if (data == null || ch == null) return null;
        return data.get(ch.getType().name().toUpperCase());
    }

    public Pair<Integer, Integer> getAttacksValues (Entity ch) {
        var data = this.getAttacksAndBaseValues(ch);
        if (data == null || ch == null) return new Pair<>(20,20);
        List <Integer> attacks = data.get("attacks");
        if (attacks.size() != 2) return new Pair<>(0,0);
        return new Pair<>(attacks.get(0),attacks.get(1));
    }

    public int getRange() {
        return 0;
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
