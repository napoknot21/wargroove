package up.wargroove.core.character;

import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.utils.Constants;
import up.wargroove.utils.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public abstract class Entity {

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

    public void attack (Entity ch) {
        if (ch == null) return;
        Pair<Integer,Integer> attacks = this.getAttacksValues(ch);
        Pair<Integer,Integer> defends = ch.getDefendsValues(this);
        if (defends.first - attacks.first > 0) return;
        ch.setHealth(ch.getHealth() - attacks.first + defends.first);
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

    public Map<String, List <Integer> > getAttacksAndDefendsValues (Entity ch) {
        var data = this.readDamageMatrixValues();
        if (data == null || ch == null) return null;
        return data.get(ch.getType().name().toUpperCase());
    }

    public Pair<Integer, Integer> getAttacksValues (Entity ch) {
        var data = this.getAttacksAndDefendsValues(ch);
        if (data == null || ch == null) return new Pair<>(20,20);
        List <Integer> attacks = data.get("attacks");
        if (attacks.size() != 2) return new Pair<>(10,10);;
        return new Pair<Integer,Integer>(attacks.get(0),attacks.get(1));
    }

    public Pair<Integer,Integer> getDefendsValues (Entity ch) {
        var data = this.getAttacksAndDefendsValues(ch);
        if (data == null || ch == null) return new Pair<>(0,10);;
        List <Integer> defends = data.get("defends");
        if (defends.size() != 2) return new Pair<>(10,10);;
        return new Pair<Integer,Integer>(defends.get(0),defends.get(1));
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
