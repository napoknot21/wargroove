package up.wargroove.core.ui.views.objects;

import java.io.File;

/**
 * Represent the type of Tile related to its file name.
 */
public enum TileType {
    TEST(false, "test"),
    GRASS(true, "grass"),
    BRIDGE(true, "bridge"),
    FOREST(true, "forest"),
    MOUNTAIN(true, "mountain"),
    BEACH(true, "beach"),
    SEA(false, "sea"),
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
        return TEXTURE_PATH + biome + File.separatorChar + tile.texture + extension;
    }

    /**
     * Get the texture path.
     *
     * @param tile The type of the tile type
     * @return The texture path
     */
    private static String getTexturePath(TileType tile) {
        return TEXTURE_PATH + tile.texture + extension;
    }

    /**
     * Get the static equivalent to the given id.
     *
     * @param id The id of the tile
     * @return The Tiletype equivalent of the id
     */
    private static TileType getTileType(int id) {
        int it = 1;
        switch (id) {
            case 1:
                return GRASS;
            case 2:
                return BRIDGE;
            case 3:
                return FOREST;
            case 4:
                return MOUNTAIN;
            case 5:
                return BEACH;
            case 6:
                return SEA;
            case 7:
                return DEEP_SEA;
            case 8:
                return RIVER;
            case 9:
                return REEF;
            case 10:
                return WALL;
            case 11:
                return FLAGSTONE;
            case 12:
                return CARPET;
            default:
                return TEST;
        }
    }

    /**
     * Represent the biome of a Tile related to its directory.
     */
    public enum Biome {
        ICE, DEFAULT, LAVA
    }
}
