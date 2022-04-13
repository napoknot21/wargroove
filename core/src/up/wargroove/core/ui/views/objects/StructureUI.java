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

public class StructureUI extends Actor {
    Sprite texture;
    public StructureUI(Stage stage, Structure structure, float ratio, Pair<Integer,Integer> position) {
        super();
        setPosition(position.first * ratio, position.second * ratio);
        stage.addActor(this);
        texture = new Sprite(Assets.getInstance().getTest());
        texture.setColor(Color.SKY);
        texture.setPosition(getX(),getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        texture.draw(batch);
        super.draw(batch, parentAlpha);
    }
}
