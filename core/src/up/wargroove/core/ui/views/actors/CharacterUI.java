package up.wargroove.core.ui.views.actors;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.utils.Pair;
import java.util.ArrayList;


public class CharacterUI extends Actor {
    private Controller controller;
    private Sprite sprite;
    private Sprite spriteWaiting;
    private Sprite stats;
    private Pair<Integer, Integer> coordinate;
    private Character character;
    private TextureRegion[] animationMove;
    private TextureRegion[] animationDie;
    private float temps;
    private ArrayList<java.lang.Character> move= new ArrayList<>();
    private static float TIME_LAPSE=0.5f;
    private static final int TILE_SIZE= 20;
    private static final int DEFAULT_FRAMES= 13;
    private static int ATTACK_FRAMES;
    public boolean alive= true;


    private static final String TEXTURE_PATH = "data/sprites/character/";
    private Assets assets;



    private Texture getPath(String nameFile) {
        return assets.get(TEXTURE_PATH+character.getFaction()+ "/"+character.getType()+"_"+nameFile, Texture.class);
    }

    private Texture getPathSTATS(int file) {
        return assets.get(TEXTURE_PATH+ "STATS/Stats"+file+".png", Texture.class);
    }




    /**
     * Represent a visual character.
     */


    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character) {
        this.controller= controller;
        this.character = character;
        this.coordinate = coord;
        controller.getWorld().addEntity(coord, character);
        assets= controller.getWargroove().getAssets();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);

    }

    private void initialiseSprites(){
        AnimationDie();
        this.sprite = new Sprite(animationDie[0]);
        sprite.setSize(TILE_SIZE,TILE_SIZE+TILE_SIZE/2);
        this.stats= new Sprite(getPathSTATS(0));
        stats.setSize(sprite.getWidth()/4,sprite.getHeight()/4);
        setPosition(coordinate.first * TILE_SIZE,coordinate.second * TILE_SIZE);
        positionChanged();
        spriteWaiting=sprite;
    }

    public int actualiseStats(){
        if (character.getStats().getHealth()<100){
            this.stats= new Sprite(getPathSTATS((int) (character.getStats().getHealth()/10)));
            return 1;
        }
        if (character.getStats().getHealth()<=0){
            this.stats= new Sprite(getPathSTATS( 0));
            return 0;
        }
        return -1;
    }



    @Override
    public void positionChanged() {
        sprite.setPosition(getX(), getY());
        stats.setPosition(getX()+sprite.getWidth()-stats.getWidth()-1,getY()+1);
        super.positionChanged();
    }



    @Override
    public void act(float delta) {
        super.act(delta);
    }

    //TODO Ajout condition si la vie n'est pas a 100% (quand merge sera fait)

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!move.isEmpty()) move();
        if (move.isEmpty()) {
            //actualiseStats();
            stats.draw(batch);
        }
        //if (coordinate.first.equals(new Integer(9))) die();
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
    }



    /**
     * Decides the mouvement
     */
    public void move() {
        if (temps<TILE_SIZE){
            temps+=TIME_LAPSE;
        }
        switch (move.get(0)){
            case 'U': moveTo(0,1, getPath("MOVE_U.png")); return;
            case 'R': moveTo(1,0, getPath("MOVE_R.png")); return;
            case 'D': moveTo(0,-1, getPath("MOVE_D.png")); return;
            case 'L': moveTo(-1,0, getPath("MOVE_L.png"));
        }
    }

/**
 * According with the direction, moves the animation to a tile next of the origin tile
 */
    private void moveTo(int x, int y, Texture texture){
        if(temps==TIME_LAPSE){
            AnimationWalk(texture);
        }
        sprite= new Sprite(animationMove[(int) temps%8]);
        sprite.setSize(20,30);
        setPosition(getX()+TIME_LAPSE*x,getY()+TIME_LAPSE*y);
        if (temps>=TILE_SIZE){
            temps=0;
            coordinate.first+= x;
            coordinate.second+= y;
            setPosition(coordinate.first * TILE_SIZE,coordinate.second * TILE_SIZE);
            spriteWaiting.setPosition(getX(),getY());
            move.remove(0);
            if (move.isEmpty()){
                sprite=spriteWaiting;
            }
        }
    }



    /**
     * Create the animation showing the character walking
     */


    public void AnimationWalk(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        animationMove = new TextureRegion[8];
        for (int i = 0; i < animationMove.length ; i++){
            animationMove[i] = tmp[0][i];
        }
    }

    /**
     * Create the animation showing the character doing his attack
     */
    public void AnimationAttack(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        animationMove = new TextureRegion[8];
        for (int i = 0; i < animationMove.length ; i++){
            animationMove[i] = tmp[0][i];
        }
    }


    private void die(){
        controller.getWorld().delEntity(coordinate, character);

        if (temps<=40){
            temps+=TIME_LAPSE;
        }   else if (temps<=60){
            temps+= TIME_LAPSE/3;
        }
        sprite= new Sprite(animationDie[(int) temps/10]);
        sprite.setSize(20,30);
        positionChanged();
    }


    /**
     * Create the animation showing the character dying
     */

    public void AnimationDie(){
        Texture texture= getPath(("DIE.png"));
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        animationDie = new TextureRegion[7];
        for (int i = 0; i < animationDie.length ; i++){
            animationDie[i] = tmp[0][i];
        }
    }

    // TODO: 27/02/2022 : Amelioration du move
    public void setMove(String path) {
        for (int i = 0; i < path.length(); i++) {
            move.add(path.charAt(i));
        }
    }

    public Texture characterTextureDefault() {
        return spriteWaiting.getTexture();
    }

    public Pair<Integer, Integer> getCoordinate() {
        return coordinate;
    }

    private void defineAttackFrames(){
        switch (character.getType()){
            case ARCHER: ATTACK_FRAMES=13;
            case SOLDIER: ATTACK_FRAMES=6;
            case SPEARMAN: ATTACK_FRAMES=8;
            case AMPHIBIAN: ATTACK_FRAMES=8;
                // case MAGE: ATTACK_FRAMES= 7;
        }
    }
}
