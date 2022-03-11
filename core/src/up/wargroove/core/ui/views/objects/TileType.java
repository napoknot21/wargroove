package up.wargroove.core.ui.views.objects;

import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;

/**
 * Represent the type of Tile related to its file name.
 */
public enum TileType {
    TEST(false, "test", null),
    PLAIN(true, "plains", Tile.Type.PLAIN),
    BRIDGE(true, "bridge", Tile.Type.BRIDGE),
    FOREST(true, "forest", Tile.Type.FOREST),
    MOUNTAIN(true, "mountain", Tile.Type.MOUNTAIN),
    BEACH(true, "beach", Tile.Type.BEACH),
    SEA(false, "sea", Tile.Type.SEA),
    ROAD(false, "road", Tile.Type.SEA),
    DEEP_SEA(false, "deep_sea", Tile.Type.DEEP_SEA),
    RIVER(true, "river", Tile.Type.RIVER),
    REEF(false, "reef", Tile.Type.REEF),
    WALL(true, "wall", Tile.Type.WALL),
    FLAGSTONE(true, "flagstone", Tile.Type.FLAGSTONE),
    CARPET(false, "carpet", Tile.Type.CARPET);


    private static final String extension = ".png";
    private static final String TEXTURE_PATH = "data/sprites/world/";
    private final boolean hasVariant;
    private final String texture;
    private Tile.Type type;

    TileType(boolean hasVariant, String texture, Tile.Type type) {
        this.texture = texture;
        this.hasVariant = hasVariant;
        this.type = type;
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
        return values()[id];
    }


}
