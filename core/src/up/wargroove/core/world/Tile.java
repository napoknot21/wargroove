package up.wargroove.core.world;

<<<<<<< HEAD
public class Tile extends Component {

	static enum TaleType implements Type {

		ROAD,
		BRIDGE,
		PLAINS,
		FOREST,
		MOUNTAIN,
		BEACH,
		SEA,
		DEEP_SEA,
		RIVER,
		REEF,
		WALL,
		FLAGSTONE,
		CARPET;
	
		int defense;

		TaleType() {

			this(0);

		}

		TaleType(int defense) {

			this.defense = defense;

		}

	}
	
	private Structure structure;

	public int[] mvt_cost = new int[7]; //{walking, riding, wheels, flying, hover, water, amphibious}

	public Tile() {

		/*
		 * Initialisé par défaut sur la plaine
		 */

		this(TaleType.PLAINS);

	}

	public Tile(Type type) {
	
		super(type);

	}

	public void setType(Type type) {

		super.type = type;

	}

	public void setStructure(Structure structure) {

		this.structure = structure;

	}
=======
import up.wargroove.core.character.Entity;

import java.util.Optional;

public class Tile {

    public static final int PRIMARY_TILE_TYPE = 5;

    /*
     * Encodage de la défense et des coûts sur 32 bits
     */

    private static final int MOUNTAIN_D_COST = 0x43001100;
    private static final int FOREST_D_COST = 0x32301104;
    private static final int PLAIN_D_COST = 0x11121102;
    private static final int SEA_D_COST = 0x10001121;
    private static final int DEEP_SEA_D_COST = 0x11111;
    private static final int BEACH_D_COST = 0x91101122;
    private static final int RIVER_D_COST = 0xA2401101;
    private static final int REEF_D_COST = 0x20001142;
    private static final int ROAD_D_COST = 0x1111102;
    private static final int BRIDGE_D_COST = 0x1111122;
    private static final int WALL_D_COST = 0x0;
    private static final int FLAGSTONE_D_COST = 0x21100102;
    private static final int CARPET_D_COST = 0x21100102;
    public Optional<Entity> entity;
    private Type type;
    private Optional<Structure> structure;

    public Tile() {

        /*
         * Initialisé par défaut sur la plaine
         */

        this(Type.PLAIN);

    }

    public Tile(Type type) {

        setType(type);
        structure = Optional.empty();
        entity = Optional.empty();

    }

    public Type getType() {

        return type;

    }

    public void setType(Type type) {

        this.type = type;

    }

    public void setStructure(Structure structure) {

        this.structure = Optional.of(structure);

    }

    public void delStructure() {

        this.structure = Optional.empty();

    }

    public String toString() {

        return type.asciiFormat + "";

    }

    public enum Type {

        /*
         * Types primaires de génération procédurale
         */

        MOUNTAIN('^', MOUNTAIN_D_COST),
        FOREST(':', FOREST_D_COST),
        PLAIN('/', PLAIN_D_COST),
        SEA('o', SEA_D_COST),
        DEEP_SEA('O', DEEP_SEA_D_COST),

        // =========================================

        BEACH('_', BEACH_D_COST),
        RIVER('s', RIVER_D_COST),
        REEF('r', REEF_D_COST),
        ROAD('-', ROAD_D_COST),
        BRIDGE('h', BRIDGE_D_COST),
        WALL('H', WALL_D_COST),
        FLAGSTONE('.', FLAGSTONE_D_COST),
        CARPET('c', CARPET_D_COST);

        public final int enc;
        final char asciiFormat;

        Type(char asciiFormat, int enc) {

            this.asciiFormat = asciiFormat;
            this.enc = enc;

        }

    }
>>>>>>> 3ff8bba9b6dcded30e114b21ce4168a7d4de8506

}
