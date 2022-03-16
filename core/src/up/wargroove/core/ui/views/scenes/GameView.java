package up.wargroove.core.ui.views.scenes;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
import up.wargroove.core.ui.views.actors.MoveDialog;
import up.wargroove.core.ui.views.actors.StructureMenu;
import up.wargroove.core.ui.views.objects.*;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

import java.util.LinkedList;


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
    private Viewport viewport;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private TileIndicator tileIndicator;
    private UnitIndicator unitIndicator;
    private StructureMenu structureMenu;
    private Stage gameViewUi;
    private SnapshotArray<InputProcessor> lastInputs;

    private boolean movement;
    private MoveDialog moveDialog;
    private Actor scopedEntity;
    /**
     * The current character possible movement.
     */
    private MovementSelector movementSelector;


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
        initGameViewUI();
        initMap();
        structureMenu = new StructureMenu(getAssets(), getController());
        movementSelector = new MovementSelector(gameMap.getScale());
        Character character = new Character(
                "Superman", Faction.CHERRYSTONE_KINGDOM, Entity.Type.ARCHER,
                0, 0, false, null
        );
        Character c = new Character(
                "Superman", Faction.CHERRYSTONE_KINGDOM, Entity.Type.ARCHER,
                0, 0, false, null
        );

        CharacterUI pepito = new CharacterUI(getController(),  new Pair<>(10, 10), character);
        CharacterUI menganito= new CharacterUI(getController(),  new Pair<>(10, 11), c);
        //pepito.moveNorth();
        //pepito.moveNorth();
        //menganito.moveEast();
        //menganito.moveSouth();
        //pepito.moveWest();
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
        tileIndicator = new TileIndicator(getController(),Biome.ICE);
        unitIndicator = new UnitIndicator(getController(), Biome.ICE);
        moveDialog = new MoveDialog(getAssets(),getController());
        Viewport viewport = new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameViewUi = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);
        Table buttons = new Table();
        buttons.bottom().add(moveDialog);

        Table indicators = new Table();
        indicators.bottom().right();
        indicators.add(unitIndicator).pad(10);
        indicators.add(tileIndicator).pad(10);


        table.add(buttons).expand().left().bottom().pad(10);
        table.add(indicators).expand().right().bottom();
        gameViewUi.addActor(table);
        addInput(gameViewUi);
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
                if (tile.getStructure().isPresent()) {
                    moveDialog.addBuy();
                    return true;
                }
                tileIndicator.setTexture(getAssets(), tile);
                unitIndicator.setTexture(getAssets(), tile);
                movement = getController().showMovements(movement, movementSelector, worldPosition);
                if (movement) {
                    scopeEntity(worldPosition);
                    moveDialog.addWait();
                }
                if (movementSelector.getPath().length() > 0) moveDialog.addMove();
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
        movementSelector.draw(getBatch());
        getStage().draw();
        structureMenu.draw();
    }

    @Override
    public void dispose() {
        gameMap = null;
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        gameViewUi.getViewport().update(width,height);
        camera.position.set(gameMap.getCenter());
        structureMenu.resize(width,height);
        super.resize(width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
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
        structureMenu.setDebug(debug);
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

    public void showsStructureMenu(LinkedList<Character> characters) {
        structureMenu.shows(characters,getAssets());
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

    public MoveDialog getMoveDialog() {
        return moveDialog;
    }

    public Cursor getCursor() {
        return cursor;
    }
}
