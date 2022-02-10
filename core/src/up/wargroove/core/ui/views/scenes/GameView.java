package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.actors.CharacterUI;
import up.wargroove.core.ui.views.objects.GameMap;
import up.wargroove.core.world.World;

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

    private static final int DEFAULT_ZOOM = 10;

    /**
     * Create the game screen.
     *
     * @param model     The wargroove's model.
     * @param wargroove The client.
     */
    public GameView(Model model, Controller controller, WargrooveClient wargroove) {
        super(controller,model, wargroove);
        getController().setScreen(this);
    }

    @Override
    public void init() {
        getModel().startGame();
        gameMap = new GameMap(getModel().getWorld(), getAssets());
        camera = new OrthographicCamera();
        World world = getModel().getWorld();
        int x = world.getDimension().first;
        int y = world.getDimension().second;
        viewport = new StretchViewport(x, y, camera);
        viewport.apply();
        camera.position.set(gameMap.getCenter());
        camera.zoom = DEFAULT_ZOOM;
        renderer = new OrthogonalTiledMapRenderer(gameMap, getBatch());
        renderer.setView(camera);
        setStage(viewport);
        addActor(CharacterUI.createActors(gameMap,this,20));
        /*
        Table table = new Table();
        table.add(new CharacterUI(gameMap,this,0,0,
                new Character("Superman", Character.Faction.CHERRRYSTONE_KINGDOM,)));

        table.add(new CharacterUI(gameMap,this,5,5));
        addActor(table);
*/
        initInput();
    }

    /**
     * Initializes the user inputs.
     */
    private void initInput() {
        InputMultiplexer input = new InputMultiplexer() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                getController().zoom(amountX, amountY, camera);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                getController().drag(pointer, camera);
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
        getStage().draw();

    }

    @Override
    public void dispose() {
        gameMap = null;
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(gameMap.getCenter());
        super.resize(width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public StretchViewport getViewport() {
        return viewport;
    }
}
