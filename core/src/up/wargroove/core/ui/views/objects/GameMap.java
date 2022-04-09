package up.wargroove.core.ui.views.objects;

import java.util.Optional;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Structure;
import up.wargroove.core.world.World;

/**
 * Represent the visual of the world.
 */
public class GameMap extends TiledMap {
    int height;
    int width;
    int tileWidth;
    int tileHeight;
    /**
     * scale pixels for one tile.
     */
    int scale;
    TiledMapTileLayer tileLayer;

    /**
     * Init the tiledMap according to the given model.
     *
     * @param world The world that will be on the gui
     */
    public GameMap(World world, Assets assets) {
        super();
        width = world.getDimension().first;
        height = world.getDimension().second;
        initDimension();
        tileLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new MapTile(world.at(i, j), assets));
                tileLayer.setCell(i, j, cell);
            }
        }
        world.at(0, 0).entity = Optional.of(new Recruitment(Recruitment.Type.BARRACKS));
        this.getLayers().add(tileLayer);
    }

    /**
     * Update the graphics scale and the tile dimension according to the viewport dimension.
     */
    private void initDimension() {
        tileHeight = 20;
        tileWidth = 20;
        scale = Math.max(tileHeight, tileWidth);
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

    public TiledMapTileLayer getTileLayer() {
        return tileLayer;
    }

    public int getScale() {
        return scale;
    }

}
