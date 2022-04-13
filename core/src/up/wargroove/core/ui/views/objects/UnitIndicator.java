package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Structure;
import up.wargroove.core.world.Tile;

/**
 * Game View Unit Indicator.
 *
 * @see Indicator
 */
public class UnitIndicator extends Indicator {

    private Sprite Stats;

    /**
     * Create a unit indicator.
     *
     * @param biome      the world biome.
     */
    public UnitIndicator(Biome biome) {
        super(biome);
        this.Stats = new Sprite();
        Stats.setSize(12, 12);

    }




    @Override
    public void setTexture(Assets assets, Tile tile) {
        if (tile.entity.isEmpty()) {
            setForeground((Texture) null);
            setBackground((Texture) null);
            setStats(null);
            return;
        }
        setBackground(getAtlas().findRegion(tile.getType().name().toLowerCase()));
        var character = tile.entity.get();
        if (character instanceof Structure) return; //Todo: structure case
        var texture = assets.get(
                "data/sprites/character/" + character.getFaction() + "/"
                        + character.getType() + "_DIE.png",
                Texture.class
        );
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 13, texture.getHeight());
        setForeground((tmp[0][0]));
        int numero=0;
        if (character.getHealth()<90){
            numero= (int) ((character.getHealth()/10)+1);
        }
        if ((character.getHealth()<=0)||(character.getHealth()==90)){
            numero= (int) ((character.getHealth()/10));
        }
        Texture stats = assets.get("data/sprites/character/STATS/Stats"+numero+".png");
        setStats(stats);
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
        super.draw(batch, parentAlpha);
        Stats.setPosition(getX()+36, getY()+1);
        if (Stats.getTexture() != null) {
            Stats.draw(batch);
        }


    }
}
