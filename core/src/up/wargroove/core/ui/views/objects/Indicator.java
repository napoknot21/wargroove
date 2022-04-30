package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.character.Entity;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Structure;
import up.wargroove.core.world.Tile;

/**
 * A game UI indicator.
 */
public class Indicator extends Actor {

    /**
     * The tile biome.
     */
    private final Biome biome;
    /**
     * The Tile indicator background. This shows the texture of the tile.
     */
    private Sprite foreground;
    /**
     * The tile indicator foreground. This shows the stats of the tile.
     */
    private Sprite background;
    private final Sprite Stats;


    /**
     * Create an indicator.
     *
     * @param biome the world biome.
     */
    public Indicator(Biome biome) {
        super();
        this.foreground = new Sprite();
        foreground.setSize(50, 50);
        this.background = new Sprite();
        background.setSize(50, 50);
        this.biome = biome;
        this.Stats = new Sprite();
        Stats.setSize(12, 12);
    }

    /**
     * Set the tile's information that will be shown on the indicator.
     *
     * @param assets the app assets.
     * @param tile   the tile that will be displayed.
     */
    public void setTexture(Assets assets, Tile tile) {
        if (tile.entity.isEmpty()) {
            setForeground((Texture) null);
            setBackground(Assets.getInstance().get(tile, getBiome()));
            setStats(null);
            return;
        }
        setBackground(Assets.getInstance().get(tile, getBiome()));
        Entity character = tile.entity.get();
        if (character instanceof Structure) {
            setForeground(Assets.getInstance().get((Structure) character));
            return;
        }
        var texture = assets.get(
                Assets.AssetDir.CHARACTER.path() + character.getFaction() + "/"
                        + character.getType() + "_DIE.png",
                Texture.class
        );
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 13, texture.getHeight());
        setForeground((tmp[0][0]));
        int numero = 0;
        if (character.getHealth() < 90) {
            numero = (int) ((character.getHealth() / 10) + 1);
        }
        if ((character.getHealth() <= 0) || (character.getHealth() == 90)) {
            numero = (int) ((character.getHealth() / 10));
        }
        Texture stats = assets.get(Assets.AssetDir.STATS.path()+"Stats" + numero + ".png");
        setStats(stats);
    }

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
    public float getWidth() {
        return foreground.getWidth();
    }

    @Override
    public float getHeight() {
        return foreground.getHeight();
    }

    public void dispose() {
        foreground = null;
        background = null;
    }


    public void setStats(Texture texture) {
        if (texture != null) {
            Stats.setRegion(new TextureRegion(texture));
        } else {
            Stats.setTexture(null);
        }
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

        Stats.setPosition(getX() + 36, getY() + 1);
        if (Stats.getTexture() != null) {
            Stats.draw(batch);
        }
    }
}
