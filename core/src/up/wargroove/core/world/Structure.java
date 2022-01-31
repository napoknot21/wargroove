package up.wargroove.core.world;

public class Structure {

	static enum Type {

		VILLAGE,
		STRONGHOLD,
		RECRUITMENT;

	}

	protected Type type;

	public Structure(Type type) {

		this.type = type;

	}

	public Type getType() {

		return type;

	}

}
