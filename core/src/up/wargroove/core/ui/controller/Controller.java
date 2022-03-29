package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Null;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.*;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.actors.CharacterUI;
import up.wargroove.core.ui.views.objects.AttackSelector;
import up.wargroove.core.ui.views.objects.MovementSelector;
import up.wargroove.core.ui.views.scenes.*;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

import java.util.LinkedList;
import java.util.Vector;


/**
 * A basic gui controller.
 */
public class Controller {
    /**
     * The wargroove client.
     */
    private final WargrooveClient wargroove;

    /**
     * The screen to control.
     */
    private View screen;

    /**
     * The game model.
     */
    private Model model;

    private Screen previous;
    /**
     * World scale.
     */
    private float worldScale;


    private boolean sound;

    /**
     * Camera velocity.
     */
    private float settingVelocity = 0.40f;
    private float settingZoom = 0.40f;

    /**
     * Create a controller.
     *
     * @param model     The app model.
     * @param wargroove The client.
     * @param screen    The current screen.
     */
    public Controller(Model model, WargrooveClient wargroove, View screen) {
        this.wargroove = wargroove;
        this.model = model;
        this.screen = screen;
        this.previous = screen;
        this.sound = true;
    }

    /**
     * Create a controller without screen.
     *
     * @param model     The app model.
     * @param wargroove The client.
     */
    public Controller(Model model, WargrooveClient wargroove) {
        this(model, wargroove, null);
    }

    /**
     * Create a model.
     */
    public void create() {
        model = new Model();
    }

    /**
     * Starts a new game.
     */
    public void startGame() {
        Model model = getModel();
        getClient().getAssets().load();
        GameView view = new GameView(model, this, getClient());
        this.getClient().setScreen(view);
        worldScale = view.getGameMap().getScale();
    }

    /**
     * Open the menu to choose the map.
     */
    public void openMapSelection() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        this.getClient().setScreen(new SelectMap(this, model, getClient()));
    }

    public void openSettings() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        this.getClient().setScreen(new PlayerSetting(this, model, getClient()));
    }

    public void openMatchSettings() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        this.getClient().setScreen(new MatchSettings(this, model, getClient()));
    }

    public void setPrevious() {
        previous = getClient().getScreen();
    }

    public void back() {
        Screen tmp = this.getClient().getScreen();
        this.getClient().setScreen(previous);
        tmp.dispose();
    }

    public Model getModel() {
        return model;
    }

    /**
     * Do a camera zoom according to the mouse wheel.
     * <b>Mouse only</b>
     *
     * @param amountX Is ignored
     * @param amountY The mouse wheel axis.
     * @param camera  The screen camera.
     */
    public void zoom(float amountX, float amountY, OrthographicCamera camera) {
        camera.zoom += amountY * settingZoom * 50 * Gdx.graphics.getDeltaTime();
        float max = (camera.viewportHeight + camera.viewportWidth) / 2 + 5;
        camera.zoom = (camera.zoom < 1) ? 1 : Math.min(camera.zoom, max);
        camera.update();
    }

    /**
     * Drag the camera according to the user input and the configured camera velocity in the settings.
     *
     * @param pointer The pointer for the event.
     * @param camera  The screen camera.
     */
    public void drag(int pointer, OrthographicCamera camera) {
        float velocity = settingVelocity * 50 * Gdx.graphics.getDeltaTime();
        camera.translate(
                -Gdx.input.getDeltaX(pointer) * velocity, Gdx.input.getDeltaY(pointer) * velocity
        );
        camera.update();
    }

    /**
     * Set the visual setting of the controller.
     */
    private void setSetting() {
        settingZoom = 0;
        settingVelocity = 0;
    }


    public WargrooveClient getClient() {
        return wargroove;
    }

    public View getScreen() {
        return screen;
    }

    public void setScreen(View screen) {
        this.screen = screen;
    }


    public boolean isSoundOn() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    /**
     * Move the cursor on the board.
     *
     * @param screenX the x input.
     * @param screenY The y input.
     * @param camera  The Screen camera.
     * @return The new cursor's position.
     */
    public Vector3 moveCursor(int screenX, int screenY, Camera camera) {
        Vector3 v = new Vector3(screenX, screenY, 0);
        v = camera.unproject(v);
        int x = (v.x < 0) ? 0 : (int) (v.x - v.x % worldScale);
        int y = (v.y < 0) ? 0 : (int) (v.y - v.y % worldScale);
        int first = (getWorld().getDimension().first - 1) * 20;
        int second = (getWorld().getDimension().second - 1) * 20;
        x = Math.min(first, x);
        y = Math.min(second, y);
        v.set(x, y, 0);
        return v;
    }

    /**
     * Finds the tile that will be displayed by the tile indicator.
     *
     * @param vector The cursor position.
     * @return the tile that will be displayed by the tile indicator.
     */
    public Tile setIndicator(Vector3 vector) {
        return getModel().getTile(vector);

    }

    /**
     * Gets the scoped character possibles movements.
     *
     * @return A vector of all the possible movements in world terrain coordinate.
     */
    public Pair<Vector<Pair<Integer, Integer>>, Vector<Pair<Integer, Integer>>> getMovementPossibilities() {
        var valids = getWorld().validMovements();
        Vector<Pair<Integer, Integer>> vectors = new Vector<>();
        Vector<Pair<Integer, Integer>> intel = new Vector<>();
        valids.forEach(v -> {
            Pair<Integer, Integer> coord = World.intToCoordinates(v.first, getWorld().getDimension());
            vectors.add(new Pair<>(coord.first, coord.second));
            intel.add(v.second);
        });
        return new Pair<>(vectors, intel);
    }

    /**
     * Gets the scoped character possibles movements.
     *
     * @return A vector of all the possible targets in world terrain coordinate.
     */

    //TODO Changer valids pour la vrai fonction qui trouve les possibles objectifs

    public Pair<Vector<Pair<Integer, Integer>>, Vector<Pair<Integer, Integer>>> getTargetPossibilities() {
        var valids = getWorld().validMovements();
        Vector<Pair<Integer, Integer>> vectors = new Vector<>();
        Vector<Pair<Integer, Integer>> intel = new Vector<>();
        valids.forEach(v -> {
            Pair<Integer, Integer> coord = World.intToCoordinates(v.first, getWorld().getDimension());
            vectors.add(new Pair<>(coord.first, coord.second));
            intel.add(v.second);
        });
        return new Pair<>(vectors, intel);
    }

    /**
     * Scope the entity at the vector position into the world.
     *
     * @param vector The entity coordinate in world terrain coordinate.
     */
    public boolean setScopeEntity(Vector3 vector) {
        return getWorld().scopeEntity(new Pair<>((int) vector.x, (int) vector.y));
    }

    /**
     * Gets the world.
     *
     * @return The model's world
     */
    public World getWorld() {
        return getModel().getWorld();
    }

    /**
     * the world's scoped entity.
     *
     * @return the world's scoped entity.
     */
    public @Null Entity getScopedEntity() {
        return getWorld().getScopedEntity();
    }

    public int getScopedEntityMovementCost() {
        Entity entity = getScopedEntity();
        return (entity == null) ? -1 : entity.getRange();
    }

    /**
     * Manage the unit movements on the screen.
     *
     * @param movement indicate if a movement is already in progress.
     * @param movementSelector The screen movement manager.
     * @param worldPosition The position in world coordinates.
     * @return true if the movements must be drawn false otherwise.
     */
    public boolean showMovements(boolean movement, MovementSelector movementSelector, Vector3 worldPosition) {
        if (movement) {
            if (!movementSelector.isValidPosition()) {
                movementSelector.reset();
                ((GameView) getScreen()).getMoveDialog().clear();
                return false;
            }
            ((GameView) getScreen()).getCursor().setLock(true);
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            movementSelector.reset();
            ((GameView) getScreen()).getMoveDialog().clear();
            return false;
        }

        var pair1 = getMovementPossibilities();
        movementSelector.showValids(getScreen().getAssets(), pair1);
        movementSelector.setEntityInformation(worldPosition, getScopedEntityMovementCost());
        return true;
    }

    public boolean showTargets(boolean attack, AttackSelector attackSelector, Vector3 worldPosition) {
        if (attack) {
            if (!attackSelector.isValidPosition()) {
                attackSelector.reset();
                ((GameView) getScreen()).getMoveDialog().clear();
                return false;
            }
            ((GameView) getScreen()).getCursor().setLock(true);
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            attackSelector.reset();
            ((GameView) getScreen()).getMoveDialog().clear();
            return false;
        }
        var pair = getTargetPossibilities();
        attackSelector.showValids(getScreen().getAssets(), pair);
        attackSelector.setEntityInformation(worldPosition, getScopedEntityMovementCost());
        return true;
    }



    public WargrooveClient getWargroove() {
        return wargroove;
    }

    /**
     * End the units movements selection and move the units to the chosen emplacement.
     */
    public void endMoving() {
        GameView gameView = (GameView) getScreen();
        MovementSelector selector = gameView.getMovementSelector();
        AttackSelector attackSelector = gameView.getAttackSelector();
        gameView.setMovement(false);
        gameView.getCursor().setLock(false);
        String path = selector.getPath();
        Pair<Integer, Integer> destination = selector.getDestination();
        if (path.isBlank()) {
            selector.reset();
            return;
        }
        attackSelector.reset();
        selector.reset();
        getWorld().moveEntity(World.coordinatesToInt(destination, getWorld().getDimension()));
        Actor entity = gameView.getScopedEntity();
        if (entity instanceof CharacterUI) {
            ((CharacterUI) entity).setMove(path);
        }
    }

    public void endAttack() {
        GameView gameView = (GameView) getScreen();
        AttackSelector selector = gameView.getAttackSelector();
        gameView.setAttack(false);
        gameView.getCursor().setLock(false);
        String path = selector.getPath();
        Pair<Integer, Integer> position = selector.getPositionAttack();
        if (path.isBlank()) {
            selector.reset();
            return;
        }
        MovementSelector movementSelector = gameView.getMovementSelector();
        movementSelector.reset();
        selector.reset();
        Actor entity = gameView.getScopedEntity();
        if (path.length() > 1) getWorld().moveEntity(World.coordinatesToInt(position, getWorld().getDimension()));
        if (entity instanceof CharacterUI) {
            ((CharacterUI) entity).setMove(path.substring(0, path.length() - 1));
            ((CharacterUI) entity).setAttackDirection(path.charAt(path.length() - 1));
        }
    }

    /**
     * Make the unit inactive for the turn.
     */
    public void entityWait() {
        GameView gameView = (GameView) getScreen();
        MovementSelector selector = gameView.getMovementSelector();
        selector.reset();
        gameView.setMovement(false);
        gameView.setAttack(false);
        gameView.getCursor().setLock(false);
    }

    /**
     * Open the structures' menu where the player can buy characters.
     */
    public void openStructureMenu() {
        GameView gameView = (GameView) getScreen();
        LinkedList<Character> characters = new LinkedList<>();
        characters.add(new Character(
                "c1", Faction.CHERRYSTONE_KINGDOM, Entity.Type.ARCHER, 10, 5, false,
                new Stats(20.0, 75.0, 20.0, 5, new Movement(Movement.Type.WALKING))
        ));
        characters.add(new Character("c1", Faction.CHERRYSTONE_KINGDOM, Entity.Type.AMPHIBIAN, 10, 5, false,
                new Stats(20.0, 75.0, 20.0, 5, new Movement(Movement.Type.WALKING))
        ));
        gameView.showsStructureMenu(characters);
    }

    public void closeStructureMenu() {
        Gdx.input.setInputProcessor(getScreen().getInputs());
    }

    public void buy(Character c) {
        //Coordonnee structure grace au curseur
        //Appel du movementSelector(ou un derive) pour placer la piece aux cases adjacentes disponibles ?
        //bfs dans le world
    }
}
