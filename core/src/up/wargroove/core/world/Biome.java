package up.wargroove.core.world;

/**
 * List all the existing biomes.
 */
public enum Biome {

    GRASS("GR"),
    ICE("IC"),
    DESERT("DE"),
    VOLCANO("VO");

    String cid;

    Biome(String cid) {

        this.cid = cid;

    }

}
