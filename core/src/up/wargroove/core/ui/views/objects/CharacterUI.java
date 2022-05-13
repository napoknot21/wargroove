package up.wargroove.core.ui.views.objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Controller;
import up.wargroove.utils.Pair;


public class CharacterUI extends EntityUI {
    private Controller controller;
    private TextureRegion[] animationMove;
    private TextureRegion[] animationDie;
    private TextureRegion[] animationAttack;
    private String move = "";
    private java.lang.Character attackDirection;
    private static final int DEFAULT_FRAMES = 13;
    private int ATTACK_FRAMES;
    private Pair<Integer, Integer> decalage;


    /**
     * Represent a visual character.
     */


    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character, float scale) {
        super(coord, character, scale);
        this.controller = controller;
        controller.getWorld().addEntity(coord, character);
        initialiseAnimation();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);
    }

    public CharacterUI(Controller controller, Stage stage, Pair<Integer, Integer> coord, Character character, float scale) {
        super(coord, character, scale);
        this.controller = controller;
        controller.getWorld().addEntity(coord, character);
        initialiseAnimation();
        initialiseSprites();
        stage.addActor(this);
    }

    public CharacterUI(Controller controller, Stage stage, Pair<Integer, Integer> coord, Character character) {
        this(controller, stage, coord, character, 1);
    }

    public CharacterUI(Controller controller, Pair<Integer, Integer> coord, Character character) {
        super(coord, character, 1);
        this.controller = controller;
        controller.getWorld().addEntity(coord, character);
        initialiseAnimation();
        initialiseSprites();
        controller.getScreen().getStage().addActor(this);
    }

    /**
     * Initialise the sprite of a character using the first texture of animation die
     */
    @Override
    protected void initialiseSprites() {
        super.initialiseSprites();
        actualiseSprite(animationDie[0]);
    }

    /**
     * Initialise all the animations
     */
    private void initialiseAnimation() {
        defineFeatures();
        animationAttack = new TextureRegion[ATTACK_FRAMES];
        animationMove = new TextureRegion[8];
        AnimationDie();
    }


    /**
     * Change the texture for the default texture
     */
    private void actualiseTexture() {
        setSprite(new Sprite(animationDie[0]));
        positionChanged();
    }

    /**
     * Change the postion of the sprite in the new position
     */

    @Override
    public void positionChanged() {
        float centerY = (getTileSize() / 3f);
        getSprite().setPosition(getX() + decalage.first, getY() + centerY + decalage.second);
        getSprite().setSize(getSize().first, getSize().second);
        getStats().setPosition(getX() + getSprite().getWidth() - getStats().getWidth() - 1, getY() + 1);
        super.positionChanged();
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }


    /**
     * Decides movement's direction
     */
    public void moveTo() {
        if (getTemps() < getTileSize()) {
            addTemps(getTimeLapse());
        }
        switch (move.charAt(0)) {
            case 'U':
                move(0, 1, getPath("MOVE_U.png"));
                return;
            case 'R':
                move(1, 0, getPath("MOVE_R.png"));
                return;
            case 'D':
                move(0, -1, getPath("MOVE_D.png"));
                return;
            case 'L':
                move(-1, 0, getPath("MOVE_L.png"));
        }
    }

    /**
     * According to the direction, moves the animation to a tile next of the origin tile
     */
    private void move(int x, int y, Texture texture) {
        if (getTemps() == getTimeLapse()) {
            AnimationWalk(texture);
        }
        getSprite().setRegion(animationMove[(int) (getTemps() / 3) % animationMove.length]);
        setSize(getSize().first, getSize().second);
        setPosition(getX() + getTimeLapse() * x, getY() + getTimeLapse() * y);
        if (getTemps() >= getTileSize()) {
            setTemps(0);
            moveBy(x, y);
            removeFirstMove();
            if (!canMove() && attackDirection == null) {
                actualiseTexture();
            }
        }
    }


    /**
     * Create the animation showing the character walking
     */


    public void AnimationWalk(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 13, texture.getHeight());
        for (int i = 0; i < animationMove.length; i++) {
            animationMove[i] = tmp[0][i];
        }
    }

    /**
     * Decides attack's direction
     */
    public void attackTo() {
        if (getTemps() < ATTACK_FRAMES * 10) {
            addTemps(getTimeLapse());
        }
        switch (attackDirection) {
            case 'U':
                attack(getPath("ATTACK_U.png"));
                return;
            case 'R':
                attack(getPath("ATTACK_R.png"));
                return;
            case 'D':
                attack(getPath("ATTACK_D.png"));
                return;
            case 'L':
                attack(getPath("ATTACK_L.png"));
        }
    }

    /**
     * According with the direction, does the attack animation
     *
     * @param texture is the direction selected
     */

    public void attack(Texture texture) {
        if (getTemps() == getTimeLapse()) {
            AnimationAttack(texture);
        }
        if (getTemps() >= (ATTACK_FRAMES * 10 - 2 * getTimeLapse()) / 2) {
            victime.setInjured(true);
        }
        actualiseSprite(animationAttack[(int) (getTemps() / 10) % ATTACK_FRAMES]);
        if (getTemps() >= ATTACK_FRAMES * 10 - 2 * getTimeLapse()) {
            setTemps(0);
            attackDirection = null;
            actualiseTexture();
            if (victime instanceof CharacterUI) {
                victime.setReadyToAttack(true);
                victime.setTemps(0);
            }
            if ((victime instanceof StructureUI) && (!victime.getEntity().getFaction().equals(getEntity().getFaction()))) {
                setInjured(true);
            }
        }
    }


    /**
     * Create the animation showing the character doing the attack
     */
    public void AnimationAttack(Texture texture) {
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 13, texture.getHeight());
        for (int i = 0; i < animationAttack.length; i++) {
            animationAttack[i] = tmp[0][i];
        }
    }

    /**
     * The character is removed from the stage and the world
     */

    private void die() {
        addTemps(getTimeLapse());
        actualiseSprite(animationDie[(int) (getTemps() / 3 % animationDie.length)]);
        if (getTemps() > 20) {
            setTemps(0);
            this.remove();
            this.clear();
            controller.getWorld().delEntity(getCoordinates(), getEntity());
        }
    }


    /**
     * Create the animation showing the character dying
     */

    public void AnimationDie() {
        Texture texture = getPath(("DIE.png"));
        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth() / 13, texture.getHeight());
        animationDie = new TextureRegion[7];
        for (int i = 0; i < animationDie.length; i++) {
            animationDie[i] = tmp[0][i];
        }
    }

    /**
     * Calcule the final coordinates
     */

    public Pair<Integer, Integer> calculateFinalPosition() {
        Pair<Integer, Integer> finalPos = new Pair<>(getCoordinates().first, getCoordinates().second);
        String path = move;
        while (path.length() != 0) {
            switch (path.charAt(path.length() - 1)) {
                case 'U':
                    finalPos.second += 1;
                    break;
                case 'R':
                    finalPos.first += 1;
                    break;
                case 'D':
                    finalPos.second -= 1;
                    break;
                case 'L':
                    finalPos.first -= 1;
                    break;
            }
            path = path.substring(0, path.length() - 1);
        }

        return finalPos;
    }

    /**
     * @return true while the character can move
     */
    @Override
    protected boolean canMove() {
        return (move.length() > 0);
    }

    /**
     * @return true if character the have not actions to do
     */
    @Override
    public boolean isWaiting() {
        return (!canMove() && attackDirection == null);
    }

    public boolean isAttackFinish() {
        return (attackDirection == null);
    }

    /**
     * Remove the first step of the path
     */
    private void removeFirstMove() {
        move = move.substring(1);
    }

    /**
     * Define the features of the sprite according whith the character
     */
    private void defineFeatures() {
        decalage = new Pair<>(0, 0);
        switch (getEntity().getType()) {
            case COMMANDER:
                ATTACK_FRAMES = 6;
                break;
            case ARCHER:
                ATTACK_FRAMES = 13;
                break;
            case SOLDIER:
                ATTACK_FRAMES = 6;
                break;
            case SPEARMAN:
                ATTACK_FRAMES = 8;
                break;
            //case AMPHIBIAN: ATTACK_FRAMES=8; break;
            case GIANT:
                ATTACK_FRAMES = 6;
                setSize(new Pair<>(getSize().first + getSize().first / 2, getSize().second + getSize().second / 2));
                decalage.first = (int) -(getTileSize() / 4);
                break;
            case MAGE:
                ATTACK_FRAMES = 7;
                break;
            default:
        }
    }

    /**
     * @return true if the character is doing a movement or an attack
     */

    public boolean isActing() {
        return canMove() && animationAttack != null;
    }

    /**
     * @param batch       the drawer
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getReadyAttack()) {
            if (canMove()) moveTo();
            else if (attackDirection != null) attackTo();
            else exhaust();
        }
        if (!isAlive()) die();

        super.draw(batch, parentAlpha);
    }


    /***************** setters and getters *****************/

    public void setAttackDirection(java.lang.Character attackDirection) {
        this.attackDirection = attackDirection;
    }

    public void setMove(String path) {
        move = move + path;
    }

}
