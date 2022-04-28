package up.wargroove.core.ui.views.objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.utils.Pair;


public class CharacterUI extends EntityUI {
    private Controller controller;
    private Sprite spriteWaiting;
    private TextureRegion[] animationMove;
    private TextureRegion[] animationDie;
    private TextureRegion[] animationAttack;
    private String move="";
    private java.lang.Character attackDirection;
    private static final int DEFAULT_FRAMES= 13;
    private int ATTACK_FRAMES;
    private Pair<Integer,Integer> decalage;


    private void actualiseSprite(){
        setSprite(new Sprite(spriteWaiting));
        positionChanged();
    }


    /**
     * Represent a visual character.
     */


    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character, float scale) {
        super(coord,character, scale);
        this.controller= controller;
        controller.getWorld().addEntity(coord, character);
        initialiseAnimation();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);


    }

    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character) {
        super(coord,character, 1);
        this.controller= controller;
        controller.getWorld().addEntity(coord, character);
        initialiseAnimation();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);
    }

    @Override
    protected void initialiseSprites(){
        super.initialiseSprites();
        actualiseSprite(animationDie[0]);
        spriteWaiting= new Sprite(getSprite());
    }


    private void initialiseAnimation() {
        defineFeatures();
        animationAttack = new TextureRegion[ATTACK_FRAMES];
        animationMove = new TextureRegion[8];
        AnimationDie();
    }



    @Override
    public void positionChanged() {
        getSprite().setPosition(getX()+decalage.first, getY()+decalage.second);
        if(spriteWaiting!=null) spriteWaiting.setPosition(getX()+decalage.first, getY()+decalage.second);
        getStats().setPosition(getX()+getSprite().getWidth()-getStats().getWidth()-1,getY()+1);
        super.positionChanged();
    }


    @Override
    public void act(float delta) {
        super.act(delta);
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
        if (getTemps()<getTileSize()){
            addTemps(getTimeLapse());
        }
        switch (move.charAt(0)){
            case 'U': move(0,1, getPath("MOVE_U.png")); return;
            case 'R': move(1,0, getPath("MOVE_R.png")); return;
            case 'D': move(0,-1, getPath("MOVE_D.png")); return;
            case 'L': move(-1,0, getPath("MOVE_L.png"));
        }
    }

/**
 * According to the direction, moves the animation to a tile next of the origin tile
 */
    private void move(int x, int y, Texture texture){
        if(getTemps()==getTimeLapse()){
            AnimationWalk(texture);
        }
        getSprite().setRegion(animationMove[(int) (getTemps()/3) % animationMove.length]);
        getSprite().setSize(getSize().first, getSize().second);
        setPosition(getX() + getTimeLapse() * x, getY() + getTimeLapse() * y);
        if (getTemps()>=getTileSize()){
            setTemps(0);
            moveBy(x,y);
            spriteWaiting.setPosition(getX(),getY());
            removeFirstMove();
            if (!canMove() && attackDirection == null){
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
        if (getTemps()<ATTACK_FRAMES*10){
           addTemps(getTimeLapse());
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
        if(getTemps()==getTimeLapse()){
            AnimationAttack(texture);
        }
        actualiseSprite(animationAttack[(int) (getTemps()/10)%ATTACK_FRAMES]);
        if (getTemps()>=ATTACK_FRAMES*10-2*getTimeLapse()) {
            setTemps(0);
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
     *
     */

    private void die(){
        addTemps(getTimeLapse());
        actualiseSprite(animationDie[(int) (getTemps()/10)]);
        if (getTemps()>=70-getTimeLapse()){
            setTemps(0);
            setAlive(false);
            controller.getWorld().delEntity(getCoordinates(), getEntity());
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

    @Override
    protected boolean canMove(){
        return (move.length()>0);
    }
    @Override
    public boolean isWaiting() {
        return (!canMove() && attackDirection == null);
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

    private boolean isInTime(float x, float y){
        return (getTemps()>=x) && (getTemps()<=y);
    }

    //todo: decomenter quand les types seront bon (pas besoin d'utiliser le type, le switch prend en charge les enum)
    private void defineFeatures(){
        decalage = new Pair<>(0,0);
        switch (getEntity().getType()){
            case COMMANDER:
            case ARCHER: ATTACK_FRAMES=13; break;
            case SOLDIER: ATTACK_FRAMES=6; break;
            case SPEARMAN: ATTACK_FRAMES=8; break;
            //case AMPHIBIAN: ATTACK_FRAMES=8; break;
            case GIANT: ATTACK_FRAMES=6; setSize(new Pair<>((int)(1.5*getTileSize()),(int)(2.5*getTileSize())));
            decalage.first=-5; break;
            case MAGE: ATTACK_FRAMES= 7;
            default:
        }
    }
    //TODO Bug de etre en train de mourir et pouvoir faire des actions (=zombie)


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (canMove()) moveTo();
        else if (attackDirection != null) attackTo();
        else exhaust();
        //if (coordinate.first.equals(new Integer(9))&&(alive)) die();
        super.draw(batch, parentAlpha);
    }
}
