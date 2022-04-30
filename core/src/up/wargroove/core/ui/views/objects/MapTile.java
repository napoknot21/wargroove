package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;

/**
 * Represent a visual tile of the world.
 */
public class MapTile extends StaticTiledMapTile {

    /**
     * Create a static tile according to the type.
     *
     * @param tile The tile.
     */
    public MapTile(Tile tile, Biome biome) {
        super(Assets.getInstance().get(tile, biome));
    }

}
