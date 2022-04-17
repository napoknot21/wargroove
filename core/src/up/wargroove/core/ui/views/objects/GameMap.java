package up.wargroove.core.ui.views.objects;

import java.util.Optional;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Structure;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

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
    TextureAtlas atlas;

    /**
     * Init the tiledMap according to the given model.
     *
     * @param world The world that will be on the gui
     * @param stage
     */
    public GameMap(World world, Stage stage, Controller controller) {
        super();
        atlas = Assets.getInstance().get(controller.getModel().getBiome());
        width = world.getDimension().first;
        height = world.getDimension().second;
        initDimension();
        tileLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new MapTile(world.at(i, j), atlas));
                addEntityImage(stage,controller,world,i,j);
                tileLayer.setCell(i, j, cell);
            }
        }
        this.getLayers().add(tileLayer);
    }

    private void addEntityImage(Stage stage,Controller controller,World world ,int i, int j) {
        Tile tile = world.at(i,j);
        if (tile.entity.isEmpty()) return;
        if (tile.entity.get() instanceof Character) {
            new CharacterUI(controller,new Pair<>(i,j), (Character) tile.entity.get());
        } else if (tile.entity.get() instanceof Structure) {
            new StructureUI(stage,(Structure) tile.entity.get(),new Pair<>(i,j));
        }
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
