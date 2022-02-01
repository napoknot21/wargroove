package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

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
    public GameMap(int[][] board) {
        super();
        this.board = board;
        updateScale();
        TiledMapTileLayer tileLayer = new TiledMapTileLayer(board.length, board[0].length, 20, 20);
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new Tile(20, 20));
                tileLayer.setCell(i, j, cell);
            }
        }
        this.getLayers().add(tileLayer);
    }

    /**
     * Render the world.
     */
    public void render() {
        new BoardRenderer(this).render();
    }

    /**
     * Update the graphics scale and the tile dimension according to the viewport dimension.
     */
    private void updateScale() {
        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();
        tileWidth = width / 10;
        tileHeight = height / 20;
        scale = Math.max(1f / tileHeight, 1f / tileWidth);
    }

    /**
     * Camera of the world.
     */
    public static class BoardCamera extends OrthographicCamera {
        public BoardCamera() {
            setToOrtho(false, 5, 5);
        }
    }

    /**
     * Tool for rendering the world.
     */
    public class BoardRenderer extends OrthogonalTiledMapRenderer {

        public BoardRenderer(GameMap map) {
            super(map, scale);
            setView(new BoardCamera());
        }
    }
}
