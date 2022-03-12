package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Tile;

/**
 * Game View Unit Indicator.
 *
 * @see Indicator
 */
public class UnitIndicator extends Indicator {

    /**
     * Create a unit indicator.
     *
     * @param assets the app assets.
     * @param biome  the world biome.
     */
    public UnitIndicator(Assets assets, Biome biome) {
        super(assets, biome);
    }


    @Override
    public void setTexture(Assets assets, Tile tile) {
        if (tile.entity.isEmpty()) {
            setForeground((Texture) null);
            setBackground((Texture) null);
            return;
        }
        String path = TileType.getTexturePath(tile, getBiome());
        setBackground(assets.get(path, Texture.class));
        var character = (Character) tile.entity.get();
        var texture = assets.get(
                "data/sprites/character/"+character.getFaction()+ "/"+character.getType()+"_DIE.png", Texture.class
        );
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        setForeground((tmp[0][0]));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }
}
