package up.wargroove.core.ui.views.objects;

import java.io.File;

/**
 * Represent the type of Tile related to its file name.
 */
public enum TileType {
    TEST(false, "test"),
    PLAINS(true, "plains"),
    BRIDGE(true, "bridge"),
    FOREST(true, "forest"),
    MOUNTAIN(true, "mountain"),
    BEACH(true, "beach"),
    SEA(false, "sea"),
    ROAD(false, "road"),
    DEEP_SEA(false, "deep_sea"),
    RIVER(true, "river"),
    REEF(false, "reef"),
    WALL(true, "wall"),
    FLAGSTONE(true, "flagstone"),
    CARPET(false, "carpet");


    private static final String extension = ".png";
    private static final String TEXTURE_PATH = "data/sprites/world/";
    private final boolean hasVariant;
    private final String texture;

    TileType(boolean hasVariant, String texture) {
        this.texture = texture;
        this.hasVariant = hasVariant;
    }

    /**
     * Get the texture path.
     *
     * @param id The id of the tile type
     * @return The texture path
     */
    public static String getTexturePath(int id) {
        return getTexturePath(getTileType(id));
    }

    /**
     * Get the texture path.
     *
     * @param id    The id of the tile type
     * @param biome The biome of the tile
     * @return The texture path
     */
    public static String getTexturePath(int id, int biome) {
        TileType tile = getTileType(id);
        if (!tile.hasVariant) {
            return getTexturePath(tile);
        }
        return TEXTURE_PATH + Biome.values()[biome].dirName + '/' + tile.texture + extension;
    }

    /**
     * Get the texture path.
     *
     * @param tile The type of the tile type
     * @return The texture path
     */
    private static String getTexturePath(TileType tile) {
        System.out.println(tile.name());
        return TEXTURE_PATH + tile.texture + extension;
    }

    /**
     * Get the static equivalent to the given id.
     *
     * @param id The id of the tile
     * @return The Tiletype equivalent of the id
     */
    private static TileType getTileType(int id) {
        return TileType.values()[id];
    }

    /**
     * Check if the given string equals the constant name.
     *
     * @param s the string compared against the constant name.
     * @return true if the given object represents a String equivalent to this string, false otherwise.
     */
    public boolean equals(String s) {
        return toString().equals(s);
    }

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
    }
}
