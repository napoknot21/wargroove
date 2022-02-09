package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.views.actors.CharacterUI;

/**
 * Represent the visual of the world.
 */
public class GameMap extends TiledMap {
    int height;
    int width;
    int tileWidth;
    int tileHeight;
    float scale;
    int[][] board;
    TiledMapTileLayer tileLayer;
    /**
     * Init the tiledMap according to the given model.
     *
     * @param board The model that will be on the gui
     */
    public GameMap(int[][] board, Assets assets) {
        super();
        this.board = board;
        width = board.length;
        height = board[0].length;
        initDimension();
        tileLayer = new TiledMapTileLayer(board.length, board[0].length, tileWidth, tileHeight);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new Tile(board[i][j], assets));
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

    public int[][] getBoard() {
        return board;
    }

    public TiledMapTileLayer getTileLayer() {
        return tileLayer;
    }
}
