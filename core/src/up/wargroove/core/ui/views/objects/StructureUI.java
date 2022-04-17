package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.views.scenes.GameView;
import up.wargroove.core.world.Structure;
import up.wargroove.utils.Pair;

public class StructureUI extends EntityUI {
    public StructureUI(Stage stage, Structure structure, Pair<Integer,Integer> position) {
        super(position,structure);
        initialiseSprites();
        stage.addActor(this);
        actualiseSprite(Assets.getInstance().getTest());
        getSprite().setPosition(position.first * getTileSize(), position.second * getTileSize());
        Color color;
        switch (structure.getFaction()) {
            case FLORAN_TRIBES: color = Color.GREEN; break;
            case FELHEIM_LEGION: color = Color.ROYAL; break;
            case CHERRYSTONE_KINGDOM: color = Color.FIREBRICK; break;
            case HEAVENSONG_EMPIRE: color = Color.WHITE; break;
            default: color = Color.CLEAR;
        }
        getSprite().setColor(color);
    }
}
