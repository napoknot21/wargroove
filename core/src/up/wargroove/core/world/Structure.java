package up.wargroove.core.world;

public class Structure {

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

}
