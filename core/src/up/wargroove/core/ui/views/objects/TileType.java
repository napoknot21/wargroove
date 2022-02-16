package up.wargroove.core.ui.views.objects;

import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;

/**
 * Represent the type of Tile related to its file name.
 */
public enum TileType {
    TEST(false, "test"),
    PLAIN(true, "plains"),
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
     * @param tile  The id of the tile type
     * @param biome The biome of the tile
     * @return The texture path
     */
    public static String getTexturePath(Tile tile, Biome biome) {
        TileType t = getTileType(tile.getType());
        if (!t.hasVariant) {
            return getTexturePath(t);
        }
        return TEXTURE_PATH + biome.getDirName() + '/' + t.texture + extension;
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

    private static TileType getTileType(Tile.Type type) {
        try {
            return TileType.valueOf(type.name());
        } catch (IllegalArgumentException e) {
            Log.print(Log.Status.ERROR, "This Tile type doesn't exist. [" + type.name() + "]");
            return TEST;
        }
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


}
