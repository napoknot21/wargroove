package up.wargroove.core.world;

public class Structure {

	static enum Type {

		NULL,
		VILLAGE,
		STRONGHOLD,
		BARRACK,
		TOWER,
		PORT,
		HIDEOUT;

	}

	protected Type type;

	public Structure(Type type) {

		this.type = type;

	}

	public Type getType() {

		return type;

	}

}
