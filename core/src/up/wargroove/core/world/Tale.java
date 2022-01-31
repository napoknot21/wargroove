package up.wargroove.core.world;

public class Tale extends Component {

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

	public Tale() {

		/*
		 * Initialisé par défaut sur la plaine
		 */

		this(TaleType.PLAINS);

	}

	public Tale(Type type) {
	
		super(type);

	}

	public void setType(Type type) {

		super.type = type;

	}

	public void setStructure(Structure structure) {

		this.structure = structure;

	}

}
