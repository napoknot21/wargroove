package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import up.wargroove.core.ui.views.objects.GameMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;


import java.awt.event.InputEvent;

public class CharacterUI extends Actor {
    Texture texture;
    Sprite sprite;
    GameMap gameMap;
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
/*
    public CharacterUI(){
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.sprite= new Sprite(texture);
        setBounds(sprite.getX(),sprite.getY(),sprite.getWidth(),sprite.getHeight());
        this.setTouchable(Touchable.enabled);


        addListener(new InputListener(){
            public boolean move() {
                if (Gdx.input.isTouched()) {
                    MoveByAction mba = new MoveByAction();
                    mba.setAmount(sprite.getX() + Gdx.input.getX(), sprite.getY() + Gdx.input.getY());
                    mba.setDuration(5f);
                    CharacterUI.this.addAction(mba);
                }
                return true;
            }
        });
    }
*/
    public CharacterUI(GameMap gameMap){
        this.gameMap= gameMap;
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.sprite= new Sprite(texture);
        //gameMap.getLayers().get(0).getOffsetY()
        this.setTouchable(Touchable.enabled);


        addListener(new InputListener(){
            public boolean move() {
                if (Gdx.input.isTouched()) {
                    MoveByAction mba = new MoveByAction();
                    mba.setAmount(sprite.getX() + Gdx.input.getX(), sprite.getY() + Gdx.input.getY());
                    mba.setDuration(5f);
                    CharacterUI.this.addAction(mba);
                }
                return true;
            }
        });

    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }




    public CharacterUI(CharacterType characterType){
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.setTouchable(Touchable.enabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //batch.draw(texture,  0f, 0f);
        float x = gameMap.getTileLayer().getCell(0,0).getTile().getOffsetX();
        float y= gameMap.getTileLayer().getCell(0,0).getTile().getOffsetY();
        float heigth = texture.getWidth();
        float width= texture.getWidth();
        batch.draw(sprite,x,y);
        super.draw(batch,parentAlpha);

        //batch.draw(texture, Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() / 2f);
    }



}