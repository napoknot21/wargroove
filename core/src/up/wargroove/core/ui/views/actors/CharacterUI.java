package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import up.wargroove.core.ui.views.objects.GameMap;
import up.wargroove.core.ui.views.scenes.GameView;

public class CharacterUI extends Actor {
    Texture texture;
    Sprite sprite;
    GameMap gameMap;
    GameView gameView;

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
    public CharacterUI(GameMap gameMap, GameView view){
        this.gameMap= gameMap;
        this.gameView = view;
        this.texture= new Texture((Gdx.files.internal("data/sprites/character/test.png")));
        this.sprite= new Sprite(texture,55,64,30,50);
        sprite.setSize(20,30);

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
        //sprite.setScale(gameMap.getScale());
        sprite.setPosition(100,100);
        sprite.draw(batch);
        super.draw(batch,parentAlpha);
        //batch.draw(texture, Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() / 2f);
    }



}