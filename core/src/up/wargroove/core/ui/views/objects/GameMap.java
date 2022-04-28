package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.world.Structure;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

/**
 * Represent the visual of the world.
 */
public class GameMap extends TiledMap {
    private final int tileSize = 64;
    int height;
    int width;
    TiledMapTileLayer tileLayer;
    TextureAtlas atlas;
    private float scale = 1.0f;

    /**
     * Init the tiledMap according to the given model.
     *
     * @param world The world that will be on the gui
     * @param stage
     */
    public GameMap(World world, Stage stage, Controller controller, float scale, boolean structure, boolean character) {
        super();
        Model.setTileSize(tileSize);
        this.scale = scale;
        atlas = Assets.getInstance().get(controller.getModel().getBiome());
        width = world.getDimension().first;
        height = world.getDimension().second;
        tileLayer = new TiledMapTileLayer(width, height, tileSize, tileSize);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new MapTile(world.at(i, j), atlas));
                if (structure || character) {
                    addEntityImage(stage, controller, world, i, j, structure, character, scale);
                }
                tileLayer.setCell(i, j, cell);
            }
        }
        this.getLayers().add(tileLayer);
    }

    public GameMap(World world, Stage stage, Controller controller, int scale) {
        this(world,stage,controller,scale,true,true);
    }

    public GameMap(World world, Stage stage, Controller controller, boolean structure, boolean character) {
        this(world, stage, controller, 1, structure,character);
    }

    public GameMap(World world, Stage stage, Controller controller) {
        this(world, stage, controller, 1);
    }

    private void addEntityImage(Stage stage, Controller controller, World world, int i, int j, boolean structure, boolean character, float scale) {
        Tile tile = world.at(i, j);
        if (tile.entity.isEmpty()) return;
        if (character && tile.entity.get() instanceof Character) {
            new CharacterUI(controller, new Pair<>(i, j), (Character) tile.entity.get(), scale);
        } else if (structure && tile.entity.get() instanceof Structure) {
            new StructureUI(stage, (Structure) tile.entity.get(), new Pair<>(i, j), scale);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Vector3 getCenter() {
        return new Vector3((width * tileSize) / 2f, (height * tileSize) / 2f, 0);
    }

    public TiledMapTileLayer getTileLayer() {
        return tileLayer;
    }

    public int getTileSize() {
        return tileSize;
    }

    public float getScale() {
        return scale;
    }
}
