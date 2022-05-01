package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Null;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.*;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.entities.Commander;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.objects.CharacterUI;
import up.wargroove.core.ui.views.objects.AttackSelector;
import up.wargroove.core.ui.views.objects.EntityUI;
import up.wargroove.core.ui.views.objects.MovementSelector;
import up.wargroove.core.ui.views.scenes.*;
import up.wargroove.core.world.Player;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

import java.util.List;
import java.util.Optional;
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

    /**
     * World scale.
     */
    private float worldScale;

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
        getModel().startGame();
        chooseMusic();
        getClient().playMusic();
        GameView view = new GameView(this, model, getClient());
        setScreen(view);
        worldScale = view.getGameMap().getScale();
    }

    /**
     * Open the menu to choose the map.
     */
    public void openMapSelection() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        setScreen(new SelectMap(getScreen(), this, model, getClient()));
    }

    public void openSettings() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        setScreen(new Settings(getScreen(), this, model, getClient()));
    }

    public void openMatchSettings() {
        Model model = getModel();
        getClient().getAssets().load();
        setPrevious();
        setScreen(new MatchSettings(getScreen(), this, model, getClient()));
    }

    public void setPrevious() {
    }


    public void back(View previous) {
        setScreen(previous);
    }

    public Model getModel() {
        return model;
    }

    public void playSound(Sound sound) {
        if (sound != null) {
            sound.play(getClient().getSoundVolume());
        }
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
        camera.zoom += amountY * getClient().getCameraZoomVelocity() * 50 * Gdx.graphics.getDeltaTime();
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
        float velocity = getClient().getCameraVelocity() * 50 * Gdx.graphics.getDeltaTime();
        camera.translate(-Gdx.input.getDeltaX(pointer) * velocity, Gdx.input.getDeltaY(pointer) * velocity);
        camera.update();
    }


    public WargrooveClient getClient() {
        return wargroove;
    }

    public View getScreen() {
        return screen;
    }

    public void setScreen(View screen) {
        this.screen = screen;
        getClient().setScreen(screen);
    }

    private void chooseMusic() {
        if (getModel().getBiome() != null) {
            getClient().setMusic(Assets.getInstance().get(Assets.AssetDir.SOUND.getPath() + getModel().getBiome().name() + ".mp3"), true);
        } else {
            getClient().setMusic(Assets.getInstance().get(Assets.AssetDir.SOUND.getPath() + "theme.mp3"), true);
        }
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
    public Tile getTile(Vector3 vector) {
        return getModel().getTile(vector);

    }

    /**
     * Gets the scoped character possibles movements.
     *
     * @return A vector of all the possible movements in world terrain coordinate.
     */
    public Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> getMovementPossibilities() {
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
    public Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> getTargetPossibilities() {
        var valids = getWorld().validTargets();
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
    public @Null
    Entity getScopedEntity() {
        return getWorld().getScopedEntity();
    }

    public int getScopedEntityMovementCost() {
        Entity entity = getScopedEntity();
        return (entity == null) ? -1 : entity.getMovRange();
    }

    /**
     * Manage the unit movements on the screen.
     *
     * @param movement         indicate if a movement is already in progress.
     * @param attack
     * @param movementSelector The screen movement manager.
     * @param worldPosition    The position in world coordinates.
     * @return true if the movements must be drawn false otherwise.
     */
    public boolean showMovements(boolean movement, boolean attack, MovementSelector movementSelector, Vector3 worldPosition) {
        GameView g = (GameView) getScreen();
        if (movement) {
            if (!g.canAttack() && !movementSelector.isValidPosition()) {
                movementSelector.reset();
                ((GameView) getScreen()).clearMoveDialog();
                return false;
            }
            g.getCursor().setLock(true);
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            movementSelector.reset();
            g.clearMoveDialog();
            g.getCursor().setLock(false);
            return false;
        }
        if (!getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction())) {
            return false;
        }
        Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> pair1 = getMovementPossibilities();
        movementSelector.showValids(getScreen().getAssets(), pair1);
        movementSelector.setEntityInformation(worldPosition, getScopedEntityMovementCost());
        return true;
    }

    public boolean showTargets(boolean attack, AttackSelector attackSelector, Vector3 worldPosition) {
        GameView g = (GameView) getScreen();
        if (attack) {
            if (!attackSelector.isValidPosition()) {
                attackSelector.reset();
                g.clearMoveDialog();
                return false;
            }
            g.getCursor().setLock(true);
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            attackSelector.reset();
            g.clearMoveDialog();
            return false;
        }
        if (!getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction())) {
            return false;
        }
        Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> pair = getTargetPossibilities();
        attackSelector.showValids(getScreen().getAssets(), pair);
        attackSelector.setEntityInformation(worldPosition, getScopedEntity().getRange());
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
        gameView.setMovement(false);
        gameView.getCursor().setLock(false);
        String path = selector.getPath();
        if (path.isBlank()) {
            selector.reset();
            return;
        }
        Pair<Integer, Integer> destination = selector.getDestination();
        gameView.clearSelectors();
        getWorld().getScopedEntity().exhaust();
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
        gameView.setMovement(false);
        gameView.getCursor().setLock(false);
        String path = selector.getPath();
        Pair<Integer, Integer> position = selector.getPositionAttack();
        Pair<Integer, Integer> positionTarget = selector.getDestination();
        gameView.clearSelectors();
        if (invalidDeplacementeAttack(path, position)) return;
        Actor actor = gameView.getScopedEntity();
        Tile tile = getWorld().at(position);
        if (tile.entity.isEmpty()) return;
        actualiseFocusEntity(positionTarget);
        Entity entityTarget = getWorld().getScopedEntity();
        Actor actorTarget = gameView.getCharacterUI(entityTarget);
        tile.entity.get().exhaust();
        ((CharacterUI) actor).setMove(path.substring(0, path.length() - 1));
        ((CharacterUI) actor).setAttackDirection(path.charAt(path.length() - 1));
        tile.entity.get().attack(entityTarget);
        ((EntityUI) actorTarget).setInjured(true);
        commanderDie((Character) entityTarget);

    }

    /**
     * In case the Commander is dead, the player lost the game and he is remove of the game
     *
     * @param entityTarget is the commander
     */

    private void commanderDie(Character entityTarget) {
        if (entityTarget instanceof Commander) {
            if (entityTarget.getHealth() <= 0) {
                Player player = getWorld().getPlayer(entityTarget.getFaction());
                killArmyAndDestroyBases(getWorld().getPlayer(entityTarget.getFaction()));
                getWorld().removePlayer(entityTarget.getFaction());
            }
        }
    }

    private void killArmyAndDestroyBases(Player player) {
        Tile[] terrain = getWorld().getTerrain();
        for (int i = 0; i < terrain.length; i++) {
            if ((terrain[i].entity.isPresent()) && ((terrain[i].entity.get().getFaction().equals(player.getFaction())))) {
                Entity entity = terrain[i].entity.get();
                if (entity instanceof Character) {
                    getWorld().delEntity(i, entity);
                    player.removeEntity(entity);
                } else {
                    entity.setFaction(Faction.OUTLAWS);
                }
            }
        }
    }

    private boolean invalidDeplacementeAttack(String path, Pair<Integer, Integer> position) {
        GameView gameView = (GameView) getScreen();
        if (path.isBlank()) {
            gameView.getAttackSelector().reset();
            return true;
        }
        if (path.length() > 1) {
            if (getWorld().checkEntity(position)) {
                gameView.getAttackSelector().reset();
                return true;
            } else {
                getWorld().moveEntity(World.coordinatesToInt(position, getWorld().getDimension()));
                return false;
            }
        }
        return false;
    }

    private void actualiseFocusEntity(Pair<Integer, Integer> positionTarget) {
        getWorld().actualiseEntity(positionTarget);
        GameView gameView = (GameView) getScreen();
        gameView.scopeEntity(positionTarget);
    }

    /**
     * Make the unit inactive for the turn.
     */
    public void entityWait() {
        GameView gameView = (GameView) getScreen();
        gameView.clearSelectors();
        getWorld().getScopedEntity().exhaust();
        gameView.setMovement(false);
        gameView.setAttack(false);
        gameView.getCursor().setLock(false);
    }

    /**
     * Open the structures' menu where the player can buy characters.
     */
    public void openStructureMenu() {
        GameView gameView = (GameView) getScreen();
        Optional<Entity> s = getTile(gameView.getCursor().getWorldPosition()).entity;
        if (s.isEmpty() || !(s.get() instanceof Recruitment) || !(s.get().getFaction().equals(getModel().getCurrentPlayer().getFaction()))) {
            return;
        }
        Recruitment r = (Recruitment) s.get();
        List<Entity> characters = r.trainableEntityClasses();
        gameView.showsStructureMenu(characters);
    }

    /**
     * Close the structure's menu.
     */
    public void closeStructureMenu() {
        Gdx.input.setInputProcessor(getScreen().getInputs());
        ((GameView) getScreen()).getCursor().setLock(false);
    }

    /**
     * Buy the entity given in argument.
     *
     * @param c The entity's class.
     */
    public void buy(Class<? extends Entity> c) {
        GameView gameView = (GameView) getScreen();
        Vector3 v = gameView.getCursor().getWorldPosition();
        Tile t = getTile(v);
        Optional<Entity> s = t.entity;
        if (s.isEmpty() || !(s.get() instanceof Recruitment)) {
            return;
        }
        Recruitment r = (Recruitment) s.get();
        getModel().setActiveStructure(r);
        Optional<Entity> bought = r.buy(c, 0, "soldier", getModel().getCurrentPlayer().getFaction());
        if (bought.isEmpty()) {
            return;
        }
        getModel().setBoughtEntity(bought.get());
        int pos = World.coordinatesToInt(new Pair<>((int) v.x, (int) v.y), getWorld().getDimension());
        List<Integer> list = getWorld().adjacentOf(pos);
        list.removeIf(i -> getWorld().at(i).entity.isPresent());
        gameView.showsPlaceable(list);
        gameView.getCursor().setLock(false);
    }

    /**
     * Place the bought entity stored in the model
     */
    public void placeBoughtEntity() {
        GameView gameView = (GameView) getScreen();
        gameView.getMovementSelector().reset();
        Vector3 v = gameView.getCursor().getWorldPosition();
        CharacterUI c = new CharacterUI(this, new Pair<>((int) v.x, (int) v.y), (Character) getModel().getBoughtEntity());
        getModel().getCurrentPlayer().addEntity(getModel().getBoughtEntity());
        getModel().getCurrentPlayer().buy(getModel().getBoughtEntity().getCost());
        getModel().getBoughtEntity().exhaust();
        getModel().getActiveStructure().exhaust();
        gameView.setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
        gameView.getStage().addActor(c);
        gameView.getCursor().setLock(false);
    }

    public void endTurn() {
        ((GameView) getScreen()).getCursor().setLock(false);
        ((GameView) getScreen()).clearAll();
        getModel().getCurrentPlayer().nextTurn();
        getModel().nextTurn();
        ((GameView) getScreen()).setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
    }

    public void nextUnit() {
        GameView gameView = (GameView) getScreen();
        gameView.clearAll();
        Entity e = getModel().getCurrentPlayer().next();
        if (e == null) return;
        Actor ui = gameView.getCharacterUI(e);
        if (ui == null) return;
        Camera camera = gameView.getCamera();
        camera.position.set(ui.getX(), ui.getY(), camera.position.z);
        gameView.getCursor().setPosition(ui.getX(), ui.getY());
    }

    public void stopGame() {
        getModel().dispose();
    }

    public void openMainMenu() {
        getClient().stopMusic(true);
        setScreen(new MainMenu(this, getModel(), getClient()));
    }

    public void closeClient() {
        Gdx.app.exit();
    }

    public void openInGameMenu() {
        setPrevious();
        View screen = new InGameMenu(getScreen(), this, getModel(), getClient());
        setScreen(screen);
    }
}
