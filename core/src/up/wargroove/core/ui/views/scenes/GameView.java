package up.wargroove.core.ui.views.scenes;


import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.*;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.entities.Villager;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.objects.CharacterUI;
import up.wargroove.core.ui.views.objects.MoveDialog;
import up.wargroove.core.ui.views.objects.StructureMenu;
import up.wargroove.core.ui.views.objects.*;
import up.wargroove.core.world.*;
import up.wargroove.utils.Pair;

import java.util.LinkedList;
import java.util.List;


/**
 * Represent the game screen.
 */
public class GameView extends View {
    private static final int DEFAULT_ZOOM = 20;
    private Cursor cursor;
    /**
     * Visual of the world.
     */
    private GameMap gameMap;
    private Viewport viewport;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderer;
    private Indicator indicator;
    private PlayerBox playerBox;
    private Stage gameViewUi;
    private TextButton menu;

    private boolean movement;
    private boolean attack;
    private MoveDialog moveDialog;
    private Codex codex;
    private Actor scopedEntity;
    private Stage characters;
    /**
     * The current character possible movement.
     */
    private MovementSelector movementSelector;
    private AttackSelector attackSelector;

    private boolean buy;


    /**
     * Create the game screen.
     *
     * @param model     The wargroove's model.
     * @param wargroove The client.
     */
    public GameView(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
    }

    @Override
    public void init() {
        initGameViewUI();
        initMap();
        setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
        movementSelector = new MovementSelector(gameMap.getTileSize());
        attackSelector = new AttackSelector(gameMap.getTileSize());
        Texture texture = getAssets().get(Assets.AssetDir.GUI.path() + "game_cursor.png");
        cursor = new Cursor(texture, gameMap.getTileSize());
        initInput();
    }

    /**
     * Initializes the map.
     */
    private void initMap() {
        World world = getModel().getWorld();
        int x = world.getDimension().first;
        int y = world.getDimension().second;
        camera = new OrthographicCamera(x, y);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        viewport.apply();
        camera.zoom = DEFAULT_ZOOM;
        setStage(viewport);
        characters = new Stage(viewport, getBatch());
        gameMap = new GameMap(getModel().getWorld(), getStage(), characters, getController());
        renderer = new OrthogonalTiledMapRenderer(gameMap, getBatch());
        renderer.setView(camera);
        addInput(characters);


        //camera.position.set(gameMap.getCenter());

    }

    /**
     * Initiates the gameView UI above the board.
     */
    private void initGameViewUI() {
        indicator = new Indicator(getModel().getBiome());
        moveDialog = new MoveDialog(getAssets(), getController());
        menu = new TextButton("Menu", getAssets().getSkin());
        playerBox = new PlayerBox();
        codex = new Codex(getAssets(), getController());

        Viewport viewport = new ScreenViewport();
        viewport.apply();
        gameViewUi = new Stage(viewport);

        Table table = new Table();
        table.setFillParent(true);
        Table buttons = new Table();
        buttons.bottom().add(moveDialog);

        Table guide = new Table();
        guide.top().left();
        guide.add(codex);

        Table indicators = new Table();
        indicators.background(getAssets().getSkin().getDrawable("window"));
        indicators.bottom().right();
        indicators.add(indicator).pad(2);

        table.add(menu).left().top().pad(10);
        table.add(guide).left().top().pad(10);

        table.add(playerBox).right().top().pad(2);
        table.row();
        table.add(buttons).expand().left().bottom().pad(2);
        table.add();
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
                Tile tile = getController().getTile(cursor.getWorldPosition());
                indicator.setTexture(getAssets(), tile);
                if (movement) {
                    movementSelector.addMovement(getAssets(), cursor.getWorldPosition());
                }
                if (attack) {
                    attackSelector.addMovement(getAssets(), cursor.getWorldPosition());
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 vector = getController().moveCursor(screenX, screenY, camera);
                Tile tile = getController().getTile(
                        (int) (vector.x / gameMap.getTileSize()), (int) (vector.y/ gameMap.getTileSize())
                );


                if (
                        tile.entity.isPresent() && !tile.entity.get().isExhausted()
                                && (tile.entity.get() instanceof Recruitment)
                                && tile.entity.get().getFaction().equals(getModel().getCurrentPlayer().getFaction())
                ) {
                    moveDialog.addBuy();
                    clearSelectors();
                    movement = attack = false;
                    cursor.setLock(true);
                    return true;
                }
                else {
                    cursor.setLock(false);
                }

                cursor.setPosition(vector);
                Vector3 worldPosition = cursor.getWorldPosition();
                tile = getController().getTile(worldPosition);
                if (tile.entity.isPresent() && (tile.entity.get().isExhausted()
                        || (tile.entity.get() instanceof Structure))) {
                    clearSelectors();
                    clearMoveDialog();
                    movement = attack = false;
                    return true;
                }

                if (buy && movementSelector.isValidPosition(worldPosition)) {
                    cursor.setLock(true);
                    getController().placeBoughtEntity();
                    buy = false;
                    return true;
                }
                buy = false;
                indicator.setTexture(getAssets(), tile);
                movement = getController().showMovements(movement, movementSelector, worldPosition);
                attack = getController().showTargets(attack, attackSelector, worldPosition);

                if (movement) {
                    moveDialog.clear();
                    scopeEntity(worldPosition);
                    moveDialog.addWait();
                }
                if (movementSelector.getPath().length() > 0) {
                    moveDialog.addMove();
                }
                if (canAttack()) {
                    moveDialog.addAttack();
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                getController().drag(pointer, camera);
                return true;
            }
        };
        addInput(input);

        menu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(Assets.getInstance().getDefault(Sound.class));
                getController().openInGameMenu();
            }
        });
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
        gameViewUi.getViewport().apply();
        renderer.setView(camera);
        renderer.render();
        getBatch().begin();
        cursor.draw(getBatch());
        movementSelector.drawValid(getBatch());
        attackSelector.drawValid(getBatch());
        getBatch().end();
        movementSelector.draw(getBatch());
        attackSelector.draw(getBatch());
        getStage().act(delta);
        getStage().draw();
        characters.draw();
        gameViewUi.act(delta);
        gameViewUi.draw();
        if (getController().isCameraMoving()) getController().actCamera(camera);
    }

    @Override
    public void dispose() {
        gameMap.dispose();
        gameMap = null;
        cursor = null;
        viewport = null;
        camera = null;
        renderer.dispose();
        renderer = null;
        indicator.dispose();
        indicator = null;
        playerBox.dispose();
        playerBox = null;
        gameViewUi.dispose();
        menu = null;
        moveDialog = null;
        scopedEntity = null;
        movementSelector = null;
        attackSelector = null;
        super.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        gameViewUi.getViewport().update(width, height, true);
        camera.position.set(gameMap.getCenter());
        StructureMenu.resize(width, height);
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

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    @Override
    public void setDebug(boolean debug) {
        this.gameViewUi.setDebugAll(debug);
        super.setDebug(debug);
    }

    public String getMovementPath() {
        return movementSelector.getPath();
    }

    public Pair<Integer, Integer> getDestination() {
        return movementSelector.getDestination();
    }

    private void scopeEntity(Pair<Integer, Integer> worldCoordinate) {
        var array = characters.getActors();
        for (int i = 0; i < array.size; i++) {
            Actor tmp = array.get(i);
            if (tmp instanceof CharacterUI && (((CharacterUI) tmp)).getCoordinates().equals(worldCoordinate)) {
                scopedEntity = tmp;
                return;
            }
            scopedEntity = null;
        }
    }

    @Null
    public Actor getCharacterUI(Entity entity) {
        var array = (entity instanceof Character) ? characters.getActors() : getStage().getActors();
        for (int i = 0; i < array.size; i++) {
            Actor tmp = array.get(i);
            if (tmp instanceof CharacterUI && ((CharacterUI) tmp).getEntity().equals(entity)) {
                return tmp;
            } else if (tmp instanceof StructureUI && ((StructureUI) tmp).getEntity().equals(entity)) {
                return tmp;
            }
        }
        return null;
    }

    private void scopeEntity(Vector3 worldCoordinate) {
        scopeEntity(new Pair<>((int) worldCoordinate.x, (int) worldCoordinate.y));
    }

    /**
     * Shows the structures' menu where the player can buy characters.
     *
     * @param characters list of purchasable characters.
     */
    public void showsStructureMenu(List<Entity> characters) {
        StructureMenu.shows(characters, getAssets(), getController(), gameViewUi);
    }

    public Actor getScopedEntity() {
        return scopedEntity;
    }

    public MovementSelector getMovementSelector() {
        return movementSelector;
    }

    public AttackSelector getAttackSelector() {
        return attackSelector;
    }

    public MoveDialog getMoveDialog() {
        return moveDialog;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public boolean canAttack() {
        return ((attackSelector.getPath().length() > 0)
                && (scopedEntity instanceof CharacterUI)
                && (!(((CharacterUI) scopedEntity).getEntity() instanceof Villager)));
    }


    /**
     * Show the emplacement where we can put an entity.
     *
     * @param list The list of available emplacement.
     */
    public void showsPlaceable(List<Integer> list) {
        buy = true;
        List<Pair<Integer, Integer>> coordinates = new LinkedList<>();
        list.forEach(i -> coordinates.add(World.intToCoordinates(i, getModel().getWorld().getDimension())));
        movementSelector.showValid(getAssets(), coordinates);
    }

    public void clearSelectors() {
        attackSelector.reset();
        movementSelector.reset();
    }

    public void clearMoveDialog() {
        moveDialog.clear();
    }

    public void clearAll() {
        clearMoveDialog();
        clearSelectors();
    }

    public void setPlayerBoxInformations(Player currentPlayer, int round) {
        playerBox.setInformations(currentPlayer, round);
    }

    public Stage getCharacters() {
        return characters;
    }
}
