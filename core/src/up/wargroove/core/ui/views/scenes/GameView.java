package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.GameController;
import up.wargroove.core.ui.views.objects.GameMap;

/**
 * Represent the game screen.
 */
public class GameView extends View {
    /**
     * Visual of the world.
     */
    private GameMap gameMap;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private final GameController controller;

    /**
     * Create the game screen.
     *
     * @param model     The wargroove's model.
     * @param wargroove The client.
     */
    public GameView(Model model, WargrooveClient wargroove) {
        super(new GameController(model, wargroove), model, wargroove);
        getController().setScreen(this);
        controller = (GameController) getController();
    }

    @Override
    public void init() {
        getModel().startGame();
        gameMap = new GameMap(getModel().getWorld());
        camera = new OrthographicCamera();
        var world = getModel().getWorld();
        viewport = new StretchViewport(world.length, world[0].length, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        renderer = new OrthogonalTiledMapRenderer(gameMap, getBatch());
        renderer.setView(camera);
        initInput();
    }

    /**
     * Initializes the user inputs.
     */
    private void initInput() {
        InputMultiplexer input = new InputMultiplexer() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                controller.zoom(amountX, amountY, camera);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                controller.drag(pointer, camera);
                return true;
            }
        };
        input.addProcessor(getStage());
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void draw(float delta) {
        renderer.setView(camera);
        renderer.render();

    }

    @Override
    public void dispose() {
        gameMap = null;
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        super.resize(width, height);
    }
}
