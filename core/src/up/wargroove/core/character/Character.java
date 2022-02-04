package up.wargroove.core.character;

import up.wargroove.core.world.Tile;

public class Character extends Entity {

    enum Faction {
        CHERRRYSTONE_KINGDOM,
        FELHEIM_LEGION,
        FLORAN_TRIBES,
        HEAVENSONG_EMPIRE,
        OUTLAWS;
    }
    private Faction faction;

    static enum TypeUnit {
        GROUND,
        SEA,
        AIR,

        TypeUnit() {}
    }
    private TypeUnit typeUnit;

    static enum Type {

        VILLAGER(TypeUnit.GROUND),
        COMMANDER(TypeUnit.GROUND),
        WAGON(TypeUnit.GROUND),
        SPAREMAN(TypeUnit.GROUND),
        DOG(TypeUnit.GROUND),
        ARCHER(TypeUnit.GROUND),
        CAVALRY(TypeUnit.GROUND),
        SOLDIER(TypeUnit.GROUND),
        BALLISTA(TypeUnit.GROUND),
        TREBUCHET(TypeUnit.GROUND),
        GIANT(TypeUnit.GROUND),
        RIFLEMAN(TypeUnit.GROUND),
        THIEF(TypeUnit.SEA),

        BALLON(TypeUnit.AIR),
        AERONAUT(TypeUnit.AIR),
        SKY_RIDER(TypeUnit.AIR),
        DRAGON(TypeUnit.AIR),

        BRAGE(TypeUnit.SEA),
        AMPHIBIAN(TypeUnit.SEA),
        TURTLE(TypeUnit.SEA),
        HARPOON_SHIP(TypeUnit.SEA),
        WARSHIP(TypeUnit.SEA),

        CRYSTAL(TypeUnit.GROUND),
        VINE(TypeUnit.SEA),
        SPARROW_BOMB(TypeUnit.AIR),
        SHADOW_SISTER(TypeUnit.GROUND);

        Type(TypeUnit ut) {}

        public TypeUnit getTypeUnit () {
            return this.getTypeUnit();
        }
    };

    private Type type;

    private int cost;
    private int range;
    private boolean capture;
    /*
        true = yes
        false = no
    */
    private Stats stats;
    private Tile position; //A gerer avec le tableau

    Character (String name, Faction faction, Type type, int cost, int range, boolean capture, Stats stats, Tile position) {
        super(name);
        this.faction = faction;
        this.type = type;
        this.typeUnit = type.getTypeUnit();
        this.cost = cost;
        this.range = range;
        this.capture = capture;
        this.stats = stats;
        this.position = position;
    }

    public Faction getFaction() {
        return faction;
    }

    public TypeUnit getTypeUnit() {
        return typeUnit;
    }

    public Type getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public int getRange() {
        return range;
    }

    public boolean isCapture() {
        return capture;
    }

    public Stats getStats() {
        return stats;
    }

    public Tile getPosition() {
        return position;
    }

    /*
     * Quelques fonctions de bases pour les personnages
     */

    public boolean isAlive () {
        return (stats.getHealth() > 0);
    }

    public boolean attack (Character ch) {
        if (!this.isAlive() || !ch.isAlive()) return false;
        ch.stats.setHealth(ch.stats.getHealth()-(ch.stats.getDefense()*this.stats.getAttack()/100));
        return true;
    }

    // A completer...
    public boolean moving (Tile newPosition) {
        return true;
    }


}
