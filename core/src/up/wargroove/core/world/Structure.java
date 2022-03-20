package up.wargroove.core.world;

public abstract class Structure {

    protected Type type;

    protected Structure(Type type) {

        this.type = type;

    }

    public Type getType() {

        return type;

    }

    public interface Type {}

}
