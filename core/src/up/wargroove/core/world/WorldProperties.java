package up.wargroove.core.world;

import up.wargroove.utils.Pair;
import up.wargroove.utils.DbObject; 
import up.wargroove.utils.Properties;
import up.wargroove.utils.Constants;
import javax.sound.sampled.AudioInputStream;

public class WorldProperties implements Properties {

    public String name;
    public String description;

    public Pair<Integer, Integer> dimension;
    public Biome biome;
    public Tile[] terrain;
    public GeneratorProperties genProperties;

    public boolean fog;
    public AudioInputStream music;

    public void load(DbObject from) {

	    if(from == null) return;

	    description = from.get(Constants.WORLD_DESCRIPTION_DB_KEY).get();

	    int width = Integer.valueOf(
			    from.get(Constants.WORLD_WIDTH_DB_KEY).get()
			    );

	    int height = Integer.valueOf(
			    from.get(Constants.WORLD_HEIGHT_DB_KEY).get()
			    );

	    dimension = new Pair<>(width, height);

    }

    public DbObject toDBO() {

	    DbObject res = new DbObject();
	    
	    res.put(Constants.WORLD_DESCRIPTION_DB_KEY, description);
	    res.put(Constants.WORLD_WIDTH_DB_KEY, dimension.first);
	    res.put(Constants.WORLD_HEIGHT_DB_KEY, dimension.second);

	    return res;

    }

}
