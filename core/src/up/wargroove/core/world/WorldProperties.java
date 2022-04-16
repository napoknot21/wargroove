package up.wargroove.core.world;

import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;
import up.wargroove.utils.DbObject; 
import up.wargroove.utils.Savable;
import up.wargroove.utils.Constants;
import javax.sound.sampled.AudioInputStream;

public class WorldProperties implements Savable {

    private String name;
    private String description;

    public Pair<Integer, Integer> dimension;
    private Biome biome;
    public Tile[] terrain;
    public GeneratorProperties genProperties;

    private boolean fog;
    private float income;
    public AudioInputStream music;

    public int amt = 4;

	public WorldProperties(Biome biome, boolean fog, int income){
		this.biome = biome;
		this.fog = fog;
		this.income = income;
	}

	public WorldProperties() {
		this.biome = Biome.GRASS;
	}

	public Biome getBiome() {
		return biome;
	}

	public void setBiome(Biome biome) {
		this.biome = biome;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getIncome() {
		return income;
	}

	public void setIncome(float income) {
		this.income = income;
	}

	public boolean isFog() {
		return fog;
	}

	public void setFog(boolean fog) {
		this.fog = fog;
	}

	public Pair<Integer, Integer> getDimension() {
		return dimension;
	}

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
