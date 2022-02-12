package up.wargroove.core.world;

import up.wargroove.utils.Pair;



import javax.sound.sampled.AudioInputStream;

public class WorldProperties {


    public String name;
    public String description;

    public Pair<Integer, Integer> dimension;
    public Biome biome;
    public Tile[] terrain;
    public GeneratorProperties genProperties;

    public boolean fog;
    public AudioInputStream music;


}
