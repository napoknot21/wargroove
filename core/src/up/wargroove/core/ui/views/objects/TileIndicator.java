package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Tile;

/**
 * Game view tile indicator.
 */
public class TileIndicator extends Actor {

    /**
     * The Tile indicator background. This shows the texture of the tile.
     */
    private final Sprite tile;

    /**
     * The tile indicator foreground. This shows the stats of the tile.
     */
    private final Sprite tileStat;

    /**
     * The tile biome.
     */
    private final Biome biome;

    /**
     * Create a full Tile indicator.
     *
     * @param assets the app assets.
     * @param biome the world biome.
     */
    public TileIndicator(Assets assets, Biome biome) {
        super();
        this.tile = new Sprite(assets.get(Assets.AssetDir.WORLD.getPath() + "plains.png", Texture.class));
        tile.setSize(50, 50);
        this.tileStat = new Sprite();
        this.biome = biome;
    }

    /**
     * Set the tile texture and the tile stats.
     *
     * @param assets the app assets.
     * @param t the tile that will be displayed.
     */
    public void setTexture(Assets assets, Tile t) {
        String path = TileType.getTexturePath(t, biome);
        tile.setTexture(assets.get(path));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        tile.setPosition(getX(), getY());
        tile.draw(batch);
    }

    @Override
    public float getWidth() {
        return tile.getWidth();
    }

    @Override
    public float getHeight() {
        return tile.getHeight();
    }
}
