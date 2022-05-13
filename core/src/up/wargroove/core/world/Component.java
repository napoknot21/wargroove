package up.wargroove.core.world;

public abstract class Component {

	protected int cid;
	protected Type type;

	/**
	 * Constructor for Component
	 * @param type component type
	 */
	protected Component(Type type) {
		this.type = type;
	}

	/**
	 * getter for type
	 * @return the component type
	 */
	public Type getType() {
		return type;
	}

}