package up.wargroove.core.ui.views.scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.entities.*;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.actors.CharacterUI;
import up.wargroove.core.ui.views.actors.MoveDialog;
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

    private boolean movement;
    private MoveDialog moveDialog;
    private Actor scopedEntity;
    /**
     * The current character possible movement.
     */
    private MovementSelector movementSelector;

    private Music theme;


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

    private Music chooseMusic(){
        switch (getModel().getBiome()){
            case ICE : return Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/ICE.mp3"));
            case VOLCANO : return Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/VOLCANO.mp3"));
            case GRASS : return Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/GRASS.mp3"));
            case DESERT : return Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/DESERT.mp3"));
        }
        return Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/theme.mp3"));

    }

    @Override
    public void init() {
        initMap();
        initGameViewUI();

        theme = chooseMusic();
        theme.play();

        moveDialog = new MoveDialog(getAssets(),getController());
        movementSelector = new MovementSelector(gameMap.getScale());
        Character character = new Villager(
                "Superman", Faction.CHERRYSTONE_KINGDOM
        );

        CharacterUI pepito = new CharacterUI(getController(),  new Pair<>(10, 10), character);
        CharacterUI menganito= new CharacterUI(getController(),  new Pair<>(10, 11), character);
        pepito.moveNorth();
        pepito.moveNorth();
        menganito.moveEast();
        menganito.moveSouth();
        pepito.moveWest();





        Texture texture = getAssets().get(Assets.AssetDir.WORLD.getPath() + "test.png", Texture.class);
        cursor = new Cursor(texture, gameMap.getScale());
        addActor(moveDialog);
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
        unitIndicator = new UnitIndicator(getAssets(), Biome.ICE);
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
        InputAdapter input = new InputAdapter() {
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
                tileIndicator.setTexture(getAssets(), tile);
                unitIndicator.setTexture(getAssets(), tile);
                if (movement) {
                    movementSelector.addMovement(getAssets(), cursor.getWorldPosition());
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 vector = getController().moveCursor(screenX, screenY, camera);
                cursor.setPosition(vector);
                Vector3 worldPosition = cursor.getWorldPosition();
                Tile tile = getController().setIndicator(worldPosition);
                tileIndicator.setTexture(getAssets(), tile);
                unitIndicator.setTexture(getAssets(), tile);
                moveDialog.setPosition(vector.x,vector.y);
                if (!movement) {
                    scopeEntity(worldPosition);
                }
                movement = getController().showMovements(movement, movementSelector, worldPosition);
                moveDialog.setVisible(movement || movementSelector.isValidPosition());
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                getController().drag(pointer, camera);
                return true;
            }
        };
        addInput(input);
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
        movementSelector.drawValid(getBatch());
        getBatch().end();
        gameViewUi.draw();
        if (movement) {
            movementSelector.draw(getBatch());
        }
        getStage().draw();
    }

    @Override
    public void dispose() {
        gameMap = null;
        theme.dispose();
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

    public void setMovement(boolean movement) {
        this.movement = movement;
    }

    @Override
    public void setDebug(boolean debug) {
        this.gameViewUi.setDebugAll(debug);
        System.out.println(debug);
        super.setDebug(debug);
    }

    public String getMovementPath() {
        return movementSelector.getPath();
    }

    public Pair<Integer,Integer> getDestination() {
        return movementSelector.getDestination();
    }

    private void scopeEntity(Pair<Integer,Integer> worldCoordinate) {
        var array = getStage().getActors();
        for (int i = 0; i < array.size; i++) {
             Actor tmp = array.get(i);
             if (tmp instanceof CharacterUI && (((CharacterUI) tmp)).getCoordinate().equals(worldCoordinate)) {
                 scopedEntity = tmp;
                 return;
             }
             scopedEntity = null;
        }
    }

    private void scopeEntity(Vector3 worldCoordinate) {
        scopeEntity(new Pair<>((int)worldCoordinate.x,(int)worldCoordinate.y));
    }

    public Actor getScopedEntity() {
        return scopedEntity;
    }

    public MovementSelector getMovementSelector() {
        return movementSelector;
    }
}
