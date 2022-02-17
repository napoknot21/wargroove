package up.wargroove.core.ui.views.scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.actors.CharacterUI;
import up.wargroove.core.ui.views.objects.*;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

/**
 * Represent the game screen.
 */
public class GameView extends View {
    private static final int DEFAULT_ZOOM = 10;
    private Cursor cursor;
    /**
     * Visual of the world.
     */
    private GameMap gameMap;
    private StretchViewport viewport;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private TileIndicator tileIndicator;
    private UnitIndicator unitIndicator;
    private Stage gameViewUi;

    /**
     * Create the game screen.
     *
     * @param model     The wargroove's model.
     * @param wargroove The client.
     */
    public GameView(Model model, Controller controller, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        getController().setScreen(this);
    }

    @Override
    public void init() {
        initMap();
        initGameViewUI();
        Character character = new Character(
                "Superman", Faction.FELHEIM_LEGION, Entity.Type.VILLAGER,
                0, 0, false, null
        );
        //getModel().getWorld().addEntity(new Pair<>(0,0),character);
        Table table = new Table();
        table.setFillParent(true);
        CharacterUI jaimito= new CharacterUI(gameMap, this, new Pair<>(0, 0), character);
        table.add(jaimito);
        addActor(table);
        jaimito.moveNorth();
        Texture texture = getAssets().get(Assets.AssetDir.WORLD.getPath() + "test.png", Texture.class);
        cursor = new Cursor(texture, gameMap.getScale());
        initInput();
    }

    /**
     * Initializes the map.
     */
    private void initMap() {
        getModel().startGame();
        gameMap = new GameMap(getModel().getWorld(), getAssets());
        camera = new OrthographicCamera();
        World world = getModel().getWorld();
        int x = world.getDimension().first;
        int y = world.getDimension().second;
        viewport = new StretchViewport(x, y, camera);
        viewport.apply();
        //camera.position.set(gameMap.getCenter());
        camera.zoom = DEFAULT_ZOOM;
        renderer = new OrthogonalTiledMapRenderer(gameMap, getBatch());
        renderer.setView(camera);
        setStage(viewport);
    }

    /**
     * Initiates the gameView UI above the board.
     */
    private void initGameViewUI() {
        tileIndicator = new TileIndicator(getAssets(), Biome.ICE);
        unitIndicator = new UnitIndicator(getAssets(),Biome.ICE);
        Viewport viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameViewUi = new Stage(viewport);
        Table table = new Table();
        table.setFillParent(true);
        gameViewUi.addActor(table);
        table.add(unitIndicator).pad(10).width(unitIndicator.getWidth());
        table.add(tileIndicator).pad(10).width(tileIndicator.getWidth());
        table.bottom().right();
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
            public boolean mouseMoved(int screenX, int screenY) {
                Vector3 vector = getController().moveCursor(screenX, screenY, camera);
                cursor.setPosition(vector);
                Tile tile = getController().setIndicator(cursor.getWorldPosition());
                tileIndicator.setTexture(getAssets(),tile);
                unitIndicator.setTexture(getAssets(), tile);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 vector = getController().moveCursor(screenX, screenY, camera);
                cursor.setPosition(vector);
                Tile tile = getController().setIndicator(vector);
                tileIndicator.setTexture(getAssets(),tile);
                unitIndicator.setTexture(getAssets(), tile);
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
        getBatch().begin();
        cursor.draw(getBatch());
        getBatch().end();
        getStage().draw();
        gameViewUi.draw();

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

    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public void setDebug(boolean debug) {
        this.gameViewUi.setDebugAll(debug);
        System.out.println(debug);
        super.setDebug(debug);
    }
}
