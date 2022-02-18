package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.views.objects.GameMap;
import up.wargroove.core.ui.views.objects.MapTile;
import up.wargroove.core.ui.views.scenes.GameView;
import up.wargroove.utils.Pair;
import com.badlogic.gdx.graphics.g2d.Animation;


import java.io.File;
import java.util.ArrayList;

public class CharacterUI extends Actor {
    Sprite sprite;
    GameMap gameMap;
    GameView gameView;
    Pair<Integer, Integer> coordinate;
    MapTile tile;
    Character character;
    //Animation animationMove;
    float temps;
    ArrayList<Integer> move= new ArrayList<>();
    float TIME_LAPSE=0.30f;


    private Texture getPath(String nameFile) {
        return new Texture((Gdx.files.internal("data/sprites/character/" + character.getType().movement.component + "/" + character.getType() + "/" + character.getFaction() + "/" + nameFile)));

    }

/*
    public CharacterUI(Character character){
        this.texture= new Texture((Gdx.files.internal(getPath( character))));
    }
*/

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

    /**
     * Represent a visual character.
     */


    public CharacterUI(GameMap gameMap, GameView view, Pair<Integer, Integer> coord, Character character) {
        this.gameMap = gameMap;
        this.gameView = view;
        this.character = character;
        this.coordinate = coord;
        gameMap.getWorld().addEntity(coord, character);
        this.sprite = new Sprite(getPath("LIFE.png"));
        //sprite.setSize(260,30);
        sprite.setSize(20,30);
        //animation(8);
        this.tile = (MapTile) gameMap.getTileLayer().getCell(coordinate.first, coordinate.second).getTile();
    }

        @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }



    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        move();
        sprite.draw(batch);
        super.draw(batch,parentAlpha);
        //batch.draw(texture, Gdx.graphics.getWidth() / 2f,Gdx.graphics.getHeight() / 2f);
    }




    /* Tests with actions

   Problemes avec Action: Ça ne lis pas les actions, voir act() normalement à mettre sur le renderer
    * */

    public void moveNorth(){
        move.add(0);
        /*
        this.addAction(Actions.moveBy( coordinate.first*gameMap.getTileWidth(),gameMap.getTileHeight()+coordinate.second*gameMap.getTileHeight(),5f));
        this.addAction(Actions.color(Color.RED));
        this.sprite.setTexture(getPath("MOVE/tile008.png"));
        */

    }

    public void moveEast(){
        move.add(1);
        /*
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(getPath("MOVE/tile011.png"));
        mba.setDuration(5f);
        mba.setAmount( gameMap.getTileWidth(),0);
        this.addAction(mba);
*/
    }

    public void moveSouth(){
        move.add(2);
        /*
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(getPath("MOVE/tile010.png"));
        mba.setDuration(5f);
        mba.setAmount( 0,-gameMap.getTileHeight());
        this.addAction(mba);
        */
    }

    public void moveWest(){
        move.add(3);

        /*
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(getPath("MOVE/tile009.png"));
        mba.setDuration(5f);
        mba.setAmount( -gameMap.getTileWidth(),0);
        this.addAction(mba);
*/
    }

    public void die(){
        this.sprite.setTexture(getPath("DIE.png"));
    }

    public void move(){
        if (move.isEmpty()){
            return;
        }
        if (temps<20){
            temps+=TIME_LAPSE;
        }
        switch (move.get(0)){
            case 0: moveTo(0,1, getPath("MOVE/tile008.png")); return;
            case 1: moveTo(1,0,getPath("MOVE/tile011.png")); return;
            case 2: moveTo(0,-1,getPath("MOVE/tile009.png")); return;
            case 3: moveTo(-1,0,getPath("MOVE/tile010.png")); return;
        }
    }


    private void moveTo(int x, int y, Texture texture){
        if(temps==TIME_LAPSE){
            //AnimationMWalk(texture);
        }
        //sprite= (Sprite) animationMove.getKeyFrame(temps);
        sprite.setPosition(coordinate.first*gameMap.getTileWidth()+temps*x,coordinate.second*gameMap.getTileHeight()+temps*y);
        if (temps>=gameMap.getTileHeight()){
            temps=0;
            gameMap.getWorld().delEntity(coordinate, character);
            coordinate.first+=x;
            coordinate.second+=y;
            gameMap.getWorld().addEntity( coordinate, character);
            move.remove(0);

        }
    }



    /**
     * Create the animation accordig with the texture you want to show
     * DOESNT WORK
     */

/*
    public void AnimationMWalk(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/8,30);
        TextureRegion [] move= new TextureRegion[8];
        for (int i=0; i<8; i++) move[1]= tmp[0][i];
        animationMove= new Animation(0.25f,move);
    }
    */





}
