package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.io.File;

public class CharacterUI extends Actor {
    Texture texture;
/*
    private static String getPath(Character character) {
        return "data/sprites/character/" + character.getTypeUnit + File.separatorChar + character.getFaction + File.separatorChar+ fileName;
    }


    public CharacterUI(Character character){
        this.texture= new Texture((Gdx.files.internal(getPath( character))));
    }
*/

    public Texture getTexture() {
        return texture;
    }

    public CharacterUI (){
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() / 2f);
        //super.draw(batch,parentAlpha);
    }


}