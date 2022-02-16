package up.wargroove.core.world;

public abstract class Structure {

    protected Type type;

    protected Structure(Type type) {

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


}
