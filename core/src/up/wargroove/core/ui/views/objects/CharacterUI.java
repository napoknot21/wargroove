package up.wargroove.core.ui.views.objects;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.utils.Pair;


public class CharacterUI extends Actor {
    private Controller controller;
    private Sprite sprite;
    private Sprite spriteWaiting;
    private Sprite stats;
    private Pair<Integer, Integer> coordinate;
    private Character character;
    private TextureRegion[] animationMove;
    private TextureRegion[] animationDie;
    private TextureRegion[] animationAttack;
    private float temps;
    private String move="";
    private java.lang.Character attackDirection;
    private static float TIME_LAPSE=0.5f;
    private static final int TILE_SIZE= 20;
    private static final int DEFAULT_FRAMES= 13;
    private int ATTACK_FRAMES;
    private boolean alive= true;
    private boolean injured= false;
    private Pair<Integer,Integer> size;
    private Pair<Integer,Integer> decalage;

    private static final String TEXTURE_PATH = "data/sprites/character/";
    private Assets assets;

    public boolean isAlive() {
        return alive;
    }

    public void isInjured() {
        this.injured = true;
    }

    public Character getCharacter() {
        return character;
    }

    public boolean isWaiting(){
        return  (!canMove()&&attackDirection==null);
        }

    private Texture getPath(String nameFile) {
        return assets.get(TEXTURE_PATH+character.getFaction()+ "/"+character.getType()+"_"+nameFile, Texture.class);
    }

    private Texture getPathSTATS(int file) {
        return assets.get(TEXTURE_PATH+ "STATS/Stats"+file+".png", Texture.class);
    }

    private void actualiseSprite(TextureRegion textureRegion){
        sprite= new Sprite(textureRegion);
        sprite.setSize(size.first,size.second);
        positionChanged();
    }

    private void actualiseSprite(){
        sprite=spriteWaiting;
        positionChanged();
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
        initialiseAnimation();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);


    }

    private void initialiseSprites(){
        this.sprite = new Sprite(animationDie[0]);
        sprite.setSize(size.first,size.second);
        this.stats= new Sprite(getPathSTATS(0));
        stats.setSize(sprite.getWidth()/4,sprite.getHeight()/4);
        setPosition(coordinate.first * TILE_SIZE,coordinate.second * TILE_SIZE);
        positionChanged();
        spriteWaiting=sprite;


    }
    private void initialiseAnimation() {
        defineFeatures();
        animationAttack = new TextureRegion[ATTACK_FRAMES];
        animationMove = new TextureRegion[8];
        AnimationDie();
    }


        public void actualiseStats(){
        if (character.getHealth()<90){
            this.stats= new Sprite(getPathSTATS((int) ((character.getHealth()/10)+1)));
        }
        if ((character.getHealth()<=0)||(character.getHealth()==90)){
            this.stats= new Sprite(getPathSTATS((int) ((character.getHealth()/10))));
        }
        stats.setSize(TILE_SIZE/4,TILE_SIZE/4);
        stats.setPosition(coordinate.first * TILE_SIZE+TILE_SIZE-6,coordinate.second * TILE_SIZE+1);
    }



    @Override
    public void positionChanged() {
        sprite.setPosition(getX()+decalage.first, getY()+decalage.second);
        if(spriteWaiting!=null) spriteWaiting.setPosition(getX()+decalage.first, getY()+decalage.second);
        stats.setPosition(getX()+sprite.getWidth()-stats.getWidth()-1,getY()+1);
        super.positionChanged();
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (canMove()) moveTo();
        if (!canMove()&&attackDirection!=null) attackTo();
        if (!canMove()) exhaust();
        //if (coordinate.first.equals(new Integer(9))&&(alive)) die();
        if (injured) injure();
        sprite.draw(batch);
        if (isWaiting()) {
            actualiseStats();
            stats.draw(batch);
        }
        super.draw(batch, parentAlpha);

    }


    /**
     * Functions for tests
     *
     */

    public void moveNorth() {
        move+='U';
    }

    public void moveEast() {
        move+='R';
    }

    public void moveSouth() {
        move+='D';
    }

    public void moveWest() {
        move+='L';
    }



    /**
     * Decides movement's direction
     */
    public void moveTo() {
        if (temps<TILE_SIZE){
            System.out.println(move);
            temps+=TIME_LAPSE;
        }
        switch (move.charAt(0)){
            case 'U': move(0,1, getPath("MOVE_U.png")); return;
            case 'R': move(1,0, getPath("MOVE_R.png")); return;
            case 'D': move(0,-1, getPath("MOVE_D.png")); return;
            case 'L': move(-1,0, getPath("MOVE_L.png"));
        }
    }

/**
 * According with the direction, moves the animation to a tile next of the origin tile
 */
    private void move(int x, int y, Texture texture){
        if(temps==TIME_LAPSE){
            AnimationWalk(texture);
        }
        System.out.println(temps);
        sprite= new Sprite(animationMove[(int) (temps/3)%animationMove.length]);
        sprite.setSize(size.first,size.second);
        setPosition(getX()+TIME_LAPSE*x,getY()+TIME_LAPSE*y);
        if (temps>=TILE_SIZE){
            temps=0;
            coordinate.first+= x;
            coordinate.second+= y;
            setPosition(coordinate.first * TILE_SIZE,coordinate.second * TILE_SIZE);
            spriteWaiting.setPosition(getX(),getY());
            removeFirstMove();
            if (!canMove()){
                actualiseSprite();
            }
        }
    }



    /**
     * Create the animation showing the character walking
     */


    public void AnimationWalk(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        for (int i = 0; i < animationMove.length ; i++){
            animationMove[i] = tmp[0][i];
        }
    }

    /**
     * Decides attack's direction
     */
    public void attackTo() {
        if (temps<ATTACK_FRAMES*10){
            temps+=TIME_LAPSE;
        }
        switch (attackDirection){
            case 'U': attack( getPath("ATTACK_U.png")); return;
            case 'R': attack( getPath("ATTACK_R.png")); return;
            case 'D': attack( getPath("ATTACK_D.png")); return;
            case 'L': attack(getPath("ATTACK_L.png"));
        }
    }

    /**
     * According with the direction, does the attack animation
     * @param texture is the direction selected
     */

    public void attack(Texture texture){
        if(temps==TIME_LAPSE){
            AnimationAttack(texture);
        }
        actualiseSprite(animationAttack[(int) (temps/10)%ATTACK_FRAMES]);
        if (temps>=ATTACK_FRAMES*10-2*TIME_LAPSE) {
            temps = 0;
            attackDirection=null;
            actualiseSprite();
        }
    }

    /**
     * Create the animation showing the character doing the attack
     */
    public void AnimationAttack(Texture texture){
        TextureRegion [][] tmp = TextureRegion.split(texture, texture.getWidth()/13,texture.getHeight());
        for (int i = 0; i < animationAttack.length ; i++){
            animationAttack[i] = tmp[0][i];
        }
    }

    /**
     * Create a sequence of colors showing the character injured
     * ~That which does not kill us makes us stronger~ Friedrich Nietzsche
     */
    private void injure(){
        sprite.setColor(Color.RED);
        temps+=TIME_LAPSE;
        if (temps>3){
            temps=0;
            sprite.setColor(1,1,1,1);
            injured=false;
        }
    }

    /**
     * Puts the character with a dark filter.
     */
    private void exhaust(){
        if(character.isExhausted()) {
            sprite.setColor(Color.GRAY);
        } else {
            sprite.setColor(1,1,1,1);
        }
    }

    /**
     *
     */

    private void die(){
        temps+=TIME_LAPSE;
        actualiseSprite(animationDie[(int) temps/10]);
        if (temps>=70-TIME_LAPSE){
            temps=0;
            alive=false;
            controller.getWorld().delEntity(coordinate, character);
        }
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

    public void setMove(String path) {
        move=move+path;
    }

    private boolean canMove(){
        return (move.length()>0);
    }
    private void removeFirstMove(){
        move=move.substring(1);
    }

    public void setAttackDirection(java.lang.Character attackDirection) {
        this.attackDirection = attackDirection;
    }

    public Texture characterTextureDefault() {
        return spriteWaiting.getTexture();
    }

    public Pair<Integer, Integer> getCoordinate() {
        return coordinate;
    }

    private boolean isInTime(float x, float y){
        return (temps>=x) && (temps<=y);
    }

    private void defineFeatures(){
        size = new Pair<>(20,30);
        decalage = new Pair<>(0,0);
        switch (character.getType().name()){
            case "ARCHER": ATTACK_FRAMES=13; break;
            case "SOLDIER": ATTACK_FRAMES=6; break;
            case "SPEARMAN": ATTACK_FRAMES=8; break;
            case "AMPHIBIAN": ATTACK_FRAMES=8; break;
            case "GIANT": ATTACK_FRAMES=6; size.first=30;size.second=50; decalage.first=-5; break;
            case "MAGE": ATTACK_FRAMES= 7;
        }
    }
    //TODO Bug de etre en train de mourir et pouvoir faire des actions (=zombie)

}
