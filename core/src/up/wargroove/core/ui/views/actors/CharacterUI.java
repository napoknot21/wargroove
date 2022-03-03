package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.utils.Pair;
import java.util.ArrayList;


public class CharacterUI extends Actor {
    private Controller controller;
    private Sprite sprite;
    private Sprite spriteWaiting;
    private Pair<Integer, Integer> coordinate;
    private Character character;
    private TextureRegion[] animationMove;
    private float temps;
    private ArrayList<java.lang.Character> move= new ArrayList<>();
    private static float TIME_LAPSE=0.5f;
    private static final int TILE_SIZE= 20;
    private static final String TEXTURE_PATH = "data/sprites/character/";


    private Texture getPath(String nameFile) {
        return new Texture((Gdx.files.internal("data/sprites/character/" +  character.getType() + "/" + character.getFaction() + "/" + nameFile)));
    }

    private String getTexturePath(String nameFile) {
        return  TEXTURE_PATH + character.getType() + "/" + character.getFaction() + "/" + nameFile;
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


    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character) {
        this.controller= controller;
        this.character = character;
        this.coordinate = coord;
        controller.getWorld().addEntity(coord, character);
        this.sprite = new Sprite(getPath("LIFE.png"));
        sprite.setSize(20,30);
        setPosition(coord.first * TILE_SIZE,coord.second * TILE_SIZE);
        positionChanged();
        this.spriteWaiting = sprite;
        Assets assets= controller.getWargroove().getAssets();
        controller.getScreen().getStage().addActor(this);
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
        super.draw(batch, parentAlpha);
    }


    /**
     * Functions for tests
     *
     */

    public void moveNorth() {
        move.add('U');
    }

    public void moveEast() {
        move.add('R');
    }

    public void moveSouth() {
        move.add('D');
    }

    public void moveWest() {
        move.add('L');

        /*
        MoveByAction mba = new MoveByAction();
        this.sprite.setTexture(getPath("MOVE/tile009.png"));
        mba.setDuration(5f);
        mba.setAmount( -gameMap.getTileWidth(),0);
        this.addAction(mba);
*/
    }

    public void die() {
        this.sprite.setTexture(getPath("DIE.png"));
    }


    /**
     * Decides the mouvement
     */
    public void move() {
        if (move.isEmpty()){
            sprite=spriteWaiting;
            return;
        }
        if (temps<TILE_SIZE){
            temps+=TIME_LAPSE;
        }
        switch (move.get(0)){
            case 'U': moveTo(0,1, getPath("tile008.png")); return;
            case 'R': moveTo(1,0, getPath("tile011.png")); return;
            case 'D': moveTo(0,-1, getPath("tile010.png")); return;
            case 'L': moveTo(-1,0, getPath("tile009.png"));
        }
    }


    private void moveTo(int x, int y, Texture texture){
        if(temps==TIME_LAPSE){
            animationMove = AnimationWalk(texture);
            controller.getWorld().delEntity(coordinate, character);
        }
        sprite= new Sprite(animationMove[(int) temps%8]);
        sprite.setSize(20,30);
        setPosition(getX()+TIME_LAPSE*x,getY()+TIME_LAPSE*y);
        if (temps>=20f){
            temps=0;
            coordinate.first+= x;
            coordinate.second+= y;
            setPosition(coordinate.first * TILE_SIZE,coordinate.second * TILE_SIZE);
            spriteWaiting.setPosition(getX(),getY());
            controller.getWorld().addEntity( coordinate, character);
            move.remove(0);
        }
    }



    /**
     * Create the animation accordig with the texture you want to show
     *@return Frames
     */


    public TextureRegion[] AnimationWalk(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        TextureRegion [] move = new TextureRegion[8];
        for (int i = 0; i < move.length ; i++){
            move[i] = tmp[0][i];
        }
        return move;
    }

    public TextureRegion[] AnimationAttack(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        TextureRegion [] move = new TextureRegion[8];
        for (int i = 0; i < move.length ; i++){
            move[i] = tmp[0][i];
        }
        return move;
    }

    public TextureRegion[] AnimationDie(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        TextureRegion [] move = new TextureRegion[6];
        for (int i = 0; i < move.length ; i++){
            move[i] = tmp[0][i];
        }
        return move;
    }

    // TODO: 27/02/2022 : Amelioration du move
    public void setMove(String path) {
        for (int i = 0; i < path.length(); i++) {
            move.add(path.charAt(i));
        }
    }

    public Pair<Integer, Integer> getCoordinate() {
        return coordinate;
    }
}
