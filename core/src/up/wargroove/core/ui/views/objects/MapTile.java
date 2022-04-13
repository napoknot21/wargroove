package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Tile;

/**
 * Represent a visual tile of the world.
 */
public class MapTile extends StaticTiledMapTile {
    private Tile tile;

    /**
     * Creates a static tile with the given region.
     *
     * @param textureRegion the {@link TextureRegion} to use.
     */
    public MapTile(TextureRegion textureRegion) {
        super(textureRegion);
        getTextureRegion().setTexture(new Texture("data/sprites/world/test.png"));
    }

    /**
     * This shouldn't be used. <br>
     * <b> Test Only </b>
     *
     * @param width  the width of the Tile
     * @param height the height of the tile
     */
    public MapTile(int width, int height) {
        super(new TextureRegion());
        Texture texture = new Texture(getPath("test.png"));
        TextureRegion textureRegion = new TextureRegion(texture, width, height);
        setTextureRegion(textureRegion);
    }

    /**
     * Copy constructor.
     *
     * @param copy the StaticTiledMapTile to copy.
     */
    public MapTile(StaticTiledMapTile copy) {
        super(copy);
    }

    /**
     * Create a static tile according to the type.
     *
     * @param tile The tile.
     */
    public MapTile(Tile tile, TextureAtlas atlas) {
        super(new TextureRegion());
        setTextureRegion(atlas.findRegion(tile.getType().name().toLowerCase()));
    }

    /**
     * Create a static tile according to the type and set the texture dimension.
     *
     * @param type   The tile type.
     * @param width  The texture width.
     * @param height the texture height.
     */
    public MapTile(int type, int width, int height) {
        super(new TextureRegion());
        Texture texture = new Texture(TileType.getTexturePath(type));
        TextureRegion textureRegion = new TextureRegion(texture, width, height);
        setTextureRegion(textureRegion);
    }

    /**
     * Get the assets path.
     *
     * @param fileName The name of the file that will be loaded.
     * @return The path of the given file.
     */
    private static String getPath(String fileName) {
        return "data/sprites/world/" + fileName;
    }


}
