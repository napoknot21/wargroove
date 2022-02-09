package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.World;

/**
 * Represent the visual of the world.
 */
public class GameMap extends TiledMap {
    int height;
    int width;
    int tileWidth;
    int tileHeight;
    float scale;
    World world;
    TiledMapTileLayer tileLayer;
    /**
     * Init the tiledMap according to the given model.
     *
     * @param world The world that will be on the gui
     */
    public GameMap(World world, Assets assets) {
        super();
        this.world = world;
        width = world.getDimension().first;
        height = world.getDimension().second;
        initDimension();
        tileLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new MapTile(world.at(i,j), assets));
                tileLayer.setCell(i, j, cell);
            }
        }
        this.getLayers().add(tileLayer);
    }

    /**
     * Update the graphics scale and the tile dimension according to the viewport dimension.
     */
    private void initDimension() {
        tileHeight = 20;
        tileWidth = 20;
        scale = Math.max(1f / tileHeight, 1f / tileWidth);
    }

    public void update() {
        initDimension();
    }

    public int getHeight() {
        return height;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getWidth() {
        return width;
    }

    public Vector3 getCenter() {
        return new Vector3((width * tileWidth) / 2f, (height * tileWidth) / 2f, 0);
    }

    public World getWorld() {
        return world;
    }

    public TiledMapTileLayer getTileLayer() {
        return tileLayer;
    }

    public float getScale() {
        return scale;
    }
}
