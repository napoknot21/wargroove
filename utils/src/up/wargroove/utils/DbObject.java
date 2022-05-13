package up.wargroove.utils;


import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Optional;

/**
 * A database savable object.
 */
public class DbObject extends HashMap<String, DbObject> {

    private Optional<String> data = Optional.empty();

    public DbObject() {

        super();

    }

    public DbObject(String data) {

        this.data = Optional.of(data);

    }

    public DbObject(DbObject copy) {

        super(copy);
        this.data = copy.data;

    }

    /**
     * Loads the object's data from the source
     *
     * @param source the data source.
     */
    public DbObject(JsonObject source) {

        this();

        for (String key : source.keySet()) {

            JsonValue.ValueType type = source.get(key).getValueType();
            if (type == JsonValue.ValueType.OBJECT) {

                DbObject dbo = new DbObject(source.getJsonObject(key));
                put(key, dbo);
                continue;

            }

            DbObject leafData = new DbObject(source.getString(key));
            put(key, leafData);

        }

    }

    public <V> void put(String key, V value) {

        DbObject dbo;

        if (value.getClass() == DbObject.class)
            dbo = (DbObject) value;
        else
            dbo = new DbObject(value.toString());

        put(key, dbo);

    }

    public void set(String data) {

        if (size() > 0) return;
        this.data = Optional.of(data);

    }

    public boolean isData() {

        return data.isPresent();

    }

    public String get() {

        return data.get();

    }

}
