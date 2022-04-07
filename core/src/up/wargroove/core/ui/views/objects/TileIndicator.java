package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Tile;

/**
 * Game view tile indicator.
 *
 * @see Indicator
 */
public class TileIndicator extends Indicator {

    /**
     * Create a tile indicator.
     *
     * @param biome  the world biome.
     */
    public TileIndicator(Biome biome) {
        super(biome);
    }


    public void setTexture(Assets assets, Tile tile) {
        String path = TileType.getTexturePath(tile, getBiome());
        setForeground(assets.get(path, Texture.class));
    }

}
