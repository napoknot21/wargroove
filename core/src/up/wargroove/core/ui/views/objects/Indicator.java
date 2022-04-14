package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;

/**
 * A game UI indicator.
 */
public abstract class Indicator extends Actor {

    /**
     * The Tile indicator background. This shows the texture of the tile.
     */
    private Sprite foreground;

    /**
     * The tile indicator foreground. This shows the stats of the tile.
     */
    private Sprite background;

    /**
     * The tile biome.
     */
    private final Biome biome;

    private TextureAtlas atlas;


    /**
     * Create an indicator.
     *
     * @param biome  the world biome.
     */
    public Indicator(Biome biome) {
        super();
        this.foreground = new Sprite();
        foreground.setSize(50, 50);
        this.background = new Sprite();
        background.setSize(50, 50);
        this.biome = biome;
        atlas = Assets.getInstance().get(biome);
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

    /**
     * Set the foreground texture.
     *
     * @param texture The foreground texture.
     */
    public void setForeground(TextureRegion texture) {
        if (texture != null) {
            foreground.setRegion(texture);
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

    /**
     * Set the background texture.
     *
     * @param texture The background texture.
     */
    public void setBackground(TextureRegion texture) {
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

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public void dispose() {
        foreground = null;
        background = null;
    }
}
