package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import up.wargroove.core.ui.Assets;

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

    /**
     * Init the tiledMap according to the given model.
     *
     * @param board The model that will be on the gui
     */
    public GameMap(int[][] board, Assets assets) {
        super();
        this.board = board;
        initDimension();
        TiledMapTileLayer tileLayer = new TiledMapTileLayer(board.length, board[0].length, tileWidth, tileHeight);
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
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
}
