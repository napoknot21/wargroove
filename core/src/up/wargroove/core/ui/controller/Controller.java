package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.scenes.GameView;
import up.wargroove.core.ui.views.scenes.MatchSettings;
import up.wargroove.core.world.Tile;
import up.wargroove.core.ui.views.scenes.PlayerSetting;
import up.wargroove.core.ui.views.scenes.SelectMap;

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
    private Screen screen;

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
    public Controller(Model model, WargrooveClient wargroove, Screen screen) {
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

    public void openMatchSettings(){
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        this.getClient().setScreen(new MatchSettings(this, model, getClient()));
    }

    public void setPrevious(){
        previous = getClient().getScreen();
    }
    public void back(){
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

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
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
        int first = (getModel().getWorld().getDimension().first - 1) * 20;
        int second = (getModel().getWorld().getDimension().second - 1) * 20;
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
        vector.scl(1 / worldScale);
        return getModel().getTile(vector);

    }
}
