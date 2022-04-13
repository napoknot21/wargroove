package up.wargroove.core.ui.views.objects;

import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;

/**
 * Represent the type of Tile related to its file name.
 */
public enum TileType {
    PLAIN(true, "plains", Tile.Type.PLAIN, 1),
    BRIDGE(true, "bridge", Tile.Type.BRIDGE,2),
    FOREST(true, "forest", Tile.Type.FOREST,3),
    MOUNTAIN(true, "mountain", Tile.Type.MOUNTAIN,4),
    BEACH(true, "beach", Tile.Type.BEACH,5),
    SEA(false, "sea", Tile.Type.SEA,6),
    ROAD(false, "road", Tile.Type.SEA,7),
    DEEP_SEA(false, "deep_sea", Tile.Type.DEEP_SEA,8),
    RIVER(true, "river", Tile.Type.RIVER,9),
    REEF(false, "reef", Tile.Type.REEF,10),
    WALL(true, "wall", Tile.Type.WALL,11),
    FLAGSTONE(true, "flagstone", Tile.Type.FLAGSTONE,12),
    CARPET(false, "carpet", Tile.Type.CARPET,13),
    TEST(false, "test", null, 0);




    private static final String extension = ".png";
    private static final String TEXTURE_PATH = "data/sprites/world/";
    private final boolean hasVariant;
    private final String texture;
    private Tile.Type type;
    private int id;

    TileType(boolean hasVariant, String texture, Tile.Type type, int id) {
        this.texture = texture;
        this.hasVariant = hasVariant;
        this.type = type;
        this.id = id;
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
     * @return The texture path
     */
    public static Pair<Integer,Integer> getTexturePath(Tile tile) {
        TileType t = getTileType(tile.getType());
        int num = TileType.values().length;
        return new Pair<>(t.id / num, t.id % num);
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
