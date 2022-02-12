package up.wargroove.core.world;

public class Structure {

<<<<<<< HEAD
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
=======
    protected Type type;

    public Structure(Type type) {

        this.type = type;

    }

    public Type getType() {

        return type;

    }

    enum Type {

        NULL,
        VILLAGE,
        STRONGHOLD,
        BARRACK,
        TOWER,
        PORT,
        HIDEOUT

    }
>>>>>>> 3ff8bba9b6dcded30e114b21ce4168a7d4de8506

}
