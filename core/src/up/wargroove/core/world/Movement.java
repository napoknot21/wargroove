package up.wargroove.core.world;

public class Movement {

	public static final int COST_NULL  = 0;
	public static final int COST_ONE   = 1;
	public static final int COST_TWO   = 2;
	public static final int COST_THREE = 3;
	public static final int COST_FOUR  = 4;

	static enum Type {

		WALKING,
		RIDING,
		WHEELS,
		FLYING,
		HOVER,
		WATER,
		AMPHIBIOUS;

	}

	private Type type;

	public Movement(Type type) {

		this.type = type;

	}

	public Type getType() {

		return type;

	}

}
