package up.wargroove.core.ui.views.objects;

/**
 * Represent the biome of a Tile related to its directory.
 */
public enum Biome {
    GRASS("grass"),
    ICE("ice"),
    DESERT("desert"),
    VOLCANO("volcano");

    private final String dirName;

    Biome(String vo) {
        this.dirName = vo;
    }

    public String getDirName() {
        return dirName;
    }
}
