package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;



public class Character extends Actor{

    public void create() {
        Texture texture= new Texture((Gdx.files.internal(getPath("test.png"))));
    }



    private static String getPath(String fileName) {
        return "data/sprites/character/" + fileName;
    }

}

