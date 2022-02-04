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

    enum TypeUnit  {
        GROUND,
        SEA,
        AIR,
        SUMMON;

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

        CRYSTAL(TypeUnit.SUMMON),
        VINE(TypeUnit.SUMMON),
        SPARROW_BOMB(TypeUnit.SUMMON),
        SHADOW_SISTER(TypeUnit.SUMMON);

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
    private Tile position;

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



}
