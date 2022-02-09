package up.wargroove.core.world;

public class Tile {

	public static final int PRIMARY_TILE_TYPE = 5;

	public static enum Type {

		/*
		 * Types primaires de génération procédurale
		 */	
		MOUNTAIN('^'),				
		FOREST(':'),
		PLAIN('/'),	
		SEA('o'),
		DEEP_SEA('O'),		

		BEACH('_'),	
		RIVER('s'),	
		//REEF,
		ROAD('-'),
		BRIDGE('h'),	
		WALL('H'),
		FLAGSTONE('.'),
		CARPET('c');
	
		int defense;
		char asciiFormat;

		Type(char asciiFormat) {

			this(0);
			this.asciiFormat = asciiFormat;

		}

		Type(int defense) {

			this.defense = defense;

		}

	}
	
	private Type type;
	private Structure structure;

	public int[] mvt_cost = new int[7]; //{walking, riding, wheels, flying, hover, water, amphibious}

	public Tile() {

		/*
		 * Initialisé par défaut sur la plaine
		 */

		this(Type.PLAIN);

	}

	public Tile(Type type) {
	
		setType(type);	

	}

	public Type getType() {

		return type;

	}

	public void setType(Type type) {

		this.type = type;

	}

	public void setStructure(Structure structure) {

		this.structure = structure;

	}

	public String toString() {

		return new String(type.asciiFormat + "");

	}

}
