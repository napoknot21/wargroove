package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;

/**
 * A game UI indicator.
 */
public abstract class Indicator extends Actor {

    /**
     * The Tile indicator background. This shows the texture of the tile.
     */
    private final Sprite foreground;

    /**
     * The tile indicator foreground. This shows the stats of the tile.
     */
    private final Sprite background;

    /**
     * The tile biome.
     */
    private final Biome biome;


    /**
     * Create an indicator.
     *
     * @param assets the app assets.
     * @param biome  the world biome.
     */
    public Indicator(Assets assets, Biome biome) {
        super();
        this.foreground = new Sprite();
        foreground.setSize(50, 50);
        this.background = new Sprite();
        background.setSize(50, 50);
        this.biome = biome;
    }

    /**
     * Set the tile's information that will be shown on the indicator.
     *
     * @param assets the app assets.
     * @param tile   the tile that will be displayed.
     */
    public abstract void setTexture(Assets assets, Tile tile);

    public Sprite getForeground() {
        return foreground;
    }

    /**
     * Set the foreground texture.
     *
     * @param texture The foreground texture.
     */
    public void setForeground(Texture texture) {
        if (texture != null) {
            foreground.setRegion(new TextureRegion(texture));
        } else {
            foreground.setTexture(null);
        }
    }

    public Sprite getBackground() {
        return background;
    }

    /**
     * Set the background texture.
     *
     * @param texture The background texture.
     */
    public void setBackground(Texture texture) {
        if (texture != null) {
            background.setRegion(new TextureRegion(texture));
        } else {
            background.setTexture(null);
        }
    }

    public Biome getBiome() {
        return biome;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        getBackground().setPosition(getX(), getY());
        if (background.getTexture() != null) {
            background.draw(batch);
        }
        foreground.setPosition(getX(), getY());
        if (foreground.getTexture() != null) {
            foreground.draw(batch);
        }
    }


    @Override
    public float getWidth() {
        return foreground.getWidth();
    }

    @Override
    public float getHeight() {
        return foreground.getHeight();
    }
}