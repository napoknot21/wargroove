package up.wargroove.core.world;

import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;
import up.wargroove.utils.DbObject; 
import up.wargroove.utils.Savable;
import up.wargroove.utils.Constants;
import javax.sound.sampled.AudioInputStream;

public class WorldProperties implements Savable {

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
	    terrain = new Tile[width * height];

	    /*
	     * Chargement des tuiles depuis la base de données.
	     */

	    DbObject mapDBO = from.get(Constants.WORLD_MAP_DB_KEY);

	    for(int k = 0; k < terrain.length; k++) {
 
		    DbObject tileFrom = mapDBO.get(String.valueOf(k));

		    terrain[k] = new Tile();
		    terrain[k].load(tileFrom);  

	    }

    }

    public DbObject toDBO() {

	    DbObject res = new DbObject();
	    
	    res.put(Constants.WORLD_DESCRIPTION_DB_KEY, description);
	    res.put(Constants.WORLD_WIDTH_DB_KEY, dimension.first);
	    res.put(Constants.WORLD_HEIGHT_DB_KEY, dimension.second);

	    DbObject mapDBO = new DbObject();
	    int tileIndex = 0;

	    for(Tile tile : terrain) {

		    DbObject tileDBO = tile.toDBO();
		    mapDBO.put(String.valueOf(tileIndex), tileDBO);
		    tileIndex++;

	    }

	    res.put(Constants.WORLD_MAP_DB_KEY, mapDBO);

	    return res;

    }

}
