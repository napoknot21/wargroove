package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.Vector;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Null;
import org.lwjgl.Sys;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Entity;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.actors.CharacterUI;
import up.wargroove.core.ui.views.objects.MovementSelector;
import up.wargroove.core.ui.views.scenes.GameView;
import up.wargroove.core.ui.views.scenes.View;
import up.wargroove.core.ui.views.scenes.WorldSetting;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;


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

    /**
     * World scale.
     */
    private float worldScale;


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

    public void openSettings() {
        Model model = getModel();
        getClient().getAssets().load();
        this.getClient().setScreen(new WorldSetting(this, model, getClient()));
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
    public Pair<Vector<Pair<Integer, Integer>>,Vector<Pair<Integer, Integer>>> getMovementPossibilities() {
        var valids = getWorld().validMovements();
        Vector<Pair<Integer, Integer>> vectors = new Vector<>();
        Vector<Pair<Integer,Integer>> intel = new Vector<>();
        valids.forEach(v -> {
            Pair<Integer, Integer> coord = World.intToCoordinates(v.first, getWorld().getDimension());
            vectors.add(new Pair<>(coord.first, coord.second));
            intel.add(v.second);
        });
        return new Pair<>(vectors,intel);
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

    public @Null Entity getScopedEntity() {
        return getWorld().getScopedEntity();
    }

    public int getScopedEntityMovementCost() {
        Entity entity = getScopedEntity();
        return (entity == null) ? -1 : entity.getType().movementCost;
    }

    public boolean showMovements(boolean movement, MovementSelector movementSelector, Vector3 worldPosition) {
        if (!movement) {
            if (!setScopeEntity(worldPosition)) {
                return false;
            }
            var pair = getMovementPossibilities();
            movementSelector.showValids(getScreen().getAssets(), pair);
            movementSelector.setEntityInformation(worldPosition, getScopedEntityMovementCost());
            return true;
        }
        return false;
    }

    public WargrooveClient getWargroove() {
        return wargroove;
    }
    public void startMoving() {
        ((GameView)getScreen()).setMovement(true);
    }

    public void endMoving() {
        GameView gameView = (GameView)getScreen();
        MovementSelector selector = gameView.getMovementSelector();
        gameView.setMovement(false);
        String path = selector.getPath();
        //Pair<Integer,Integer> destination = selector.getDestination();
        selector.reset();
        //getWorld().moveEntity(World.coordinatesToInt(destination,getWorld().getDimension()));
        Actor entity = gameView.getScopedEntity();
        if (entity instanceof CharacterUI) {
            ((CharacterUI) entity).setMove(path);
            ((CharacterUI) entity).move();
        }
    }
}
