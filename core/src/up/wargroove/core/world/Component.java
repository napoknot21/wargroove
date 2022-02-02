package up.wargroove.core.world;

public abstract class Component {

	protected int cid;
	protected Type type;

	protected Component(Type type) {

		this.type = type;

	}

	public Type getType() {

		return type;

	}

}
