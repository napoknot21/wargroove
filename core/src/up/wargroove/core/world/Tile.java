package up.wargroove.core.world;

public class Tile extends Component {

	public static final int PRIMARY_TILE_TYPE = 5;

	static enum TileType implements Type {

	//	ROAD('-'),
		MOUNTAIN('^'),
	//	BRIDGE,	
		FOREST(':'),
		PLAINS('/'),		
		SEA('o'),
		BEACH('_'),
		RIVER('s');
	//	DEEP_SEA('O'),	
	//	REEF,
	//	WALL('H');
	//	FLAGSTONE,
	//	CARPET;
	
		int defense;
		char asciiFormat;

		TileType(char asciiFormat) {

			this(0);
			this.asciiFormat = asciiFormat;

		}

		TileType(int defense) {

			this.defense = defense;

		}

	}
	
	private Structure structure;

	public Tile() {

		/*
		 * Initialisé par défaut sur la plaine
		 */

		this(TileType.PLAINS);

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

	public String toString() {

		return new String(((TileType) type).asciiFormat + "");

	}

}
