package up.wargroove.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.entities.Commander;
import up.wargroove.core.character.entities.Villager;
import up.wargroove.core.ui.views.objects.*;
import up.wargroove.core.ui.views.scenes.*;
import up.wargroove.core.world.*;
import up.wargroove.utils.Constants;
import up.wargroove.utils.Database;
import up.wargroove.utils.Pair;

import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A basic gui controller.
 */
public class Controller {
    /**
     * The wargroove client.
     */
    private final WargrooveClient wargroove;
    private final Pair<Float, Float> cameraDestination = new Pair<>();
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
    private boolean cameraMoving = false;

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
        getClient().stopMusic(true);
        chooseMusic();
        getClient().playMusic();
        GameView view = new GameView(this, model, getClient());
        setScreen(view);
        worldScale = view.getGameMap().getTileSize();
        moveCameraToCommander();
        showNextTurnMessage();
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
        Viewport viewport = (getScreen()).getStage().getViewport();
        float tileSize = Model.getTileSize();
        float screenSize = Math.max(viewport.getScreenHeight(), viewport.getScreenWidth());
        float worldSize = Math.min(getWorld().getDimension().first, getWorld().getDimension().second);
        float min = 0.5f * screenSize/worldSize;
        float max = 1.5f * screenSize/worldSize;
        float value = camera.zoom +  amountY * getClient().getCameraZoomVelocity() *tileSize* Gdx.graphics.getDeltaTime();
        camera.zoom = MathUtils.clamp(value,min,max);
        camera.update();
    }

    /**
     * Drag the camera according to the user input and the configured camera velocity in the settings.
     *
     * @param pointer The pointer for the event.
     * @param camera  The screen camera.
     */
    public void drag(int pointer, OrthographicCamera camera) {
        float tileSize = Model.getTileSize();
        float velocity = getClient().getCameraVelocity() * tileSize * Gdx.graphics.getDeltaTime();
        float x = -Gdx.input.getDeltaX(pointer) * velocity + camera.position.x;
        float y = Gdx.input.getDeltaY(pointer) * velocity + camera.position.y;

        camera.position.x = MathUtils.clamp(x,0,camera.viewportWidth * tileSize);
        camera.position.y = MathUtils.clamp(y,0,camera.viewportHeight * tileSize);
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
            getClient().setMusic(Assets.getInstance().get(Assets.AssetDir.SOUND.path()
                    + getModel().getBiome().name() + ".mp3"), true);
        } else {
            getClient().setMusic(Assets.getInstance().get(Assets.AssetDir.SOUND.path() + "theme.mp3"), true);
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
        if (camera == null) return null;
        Vector3 v = new Vector3(screenX, screenY, 0);
        v = camera.unproject(v);
        int x = (v.x < 0) ? 0 : (int) (v.x - v.x % worldScale);
        int y = (v.y < 0) ? 0 : (int) (v.y - v.y % worldScale);
        int first = (int) ((getWorld().getDimension().first - 1) * worldScale);
        int second = (int) ((getWorld().getDimension().second - 1) * worldScale);
        x = Math.min(first, x);
        y = Math.min(second, y);
        v.set(x, y, 0);
        return v;
    }

    /**
     * Finds the tile at the vector position.
     *
     * @param vector The cursor position.
     * @return the tile that at the vector position.
     */
    public Tile getTile(Vector3 vector) {
        return getModel().getTile(vector);

    }

    /**
     * Finds the tile at the (x,y) position.
     *
     * @param x The cursor x position.
     * @param y The cursor y position.
     * @return the tile at the (x,y) position.
     */
    public Tile getTile(int x, int y) {
        return getModel().getTile(x, y);

    }

    /**
     * Gets the scoped character possibles movements.
     *
     * @return A vector of all the possible movements in world terrain coordinate.
     */
    public Pair<List<Pair<?, ?>>, List<int[]>> getMovementPossibilities() {
        Vector<int[]> valid = getWorld().validMovements();
        return computeSelectorData(valid);
    }

    /**
     * Gets the scoped character possibles movements.
     *
     * @return A vector of all the possible targets in world terrain coordinate.
     */

    public Pair<List<Pair<?, ?>>, List<int[]>> getTargetPossibilities() {
        Vector<int[]> valid = getWorld().validTargets();
        return computeSelectorData(valid);
    }

    private Pair<List<Pair<?, ?>>, List<int[]>> computeSelectorData(
            Vector<int[]> valid
    ) {
        Vector<Pair<?, ?>> vectors = new Vector<>();
        Vector<int[]> intel = new Vector<>();
        valid.forEach(v -> {
            Pair<Integer, Integer> coord = World.intToCoordinates(v[Constants.BFS_LIN], getWorld().getDimension());
            vectors.add(new Pair<>(coord.first, coord.second));
            intel.add(v);
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
     * @param movementSelector The screen movement manager.
     * @param worldPosition    The position in world coordinates.
     * @return true if the movements must be drawn false otherwise.
     */
    public boolean showMovements(boolean movement, MovementSelector movementSelector, Vector3 worldPosition) {
        GameView g = (GameView) getScreen();
        if (g.getAttackSelector().isActive()) return !g.canAttack();
        if (movement) {
            if (!g.canAttack() && !movementSelector.isValidPosition(worldPosition)) {
                movementSelector.reset();
                ((GameView) getScreen()).clearMoveDialog();
                return false;
            }
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            movementSelector.reset();
            g.clearMoveDialog();
            g.getCursor().setLock(false);
            return false;
        }
        Pair<List<Pair<?, ?>>, List<int[]>> pair1 = getMovementPossibilities();
        movementSelector.setOwner(getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction()));
        movementSelector.showValids(getScreen().getAssets(), pair1);
        movementSelector.setEntityInformation(worldPosition, getScopedEntityMovementCost());

        return movementSelector.isOwner() && !(getScopedEntity() instanceof Structure);
    }

    public boolean showTargets(boolean attack, AttackSelector attackSelector, Vector3 worldPosition) {
        GameView g = (GameView) getScreen();
        if (attack) {
            if (!attackSelector.isValidPosition(worldPosition)) {
                if (attackSelector.isPositionAvailable(worldPosition)) {
                    attackSelector.selectAttackPosition(worldPosition, g.getMovementSelector());
                    g.setMovement(false);
                } else {
                    attackSelector.reset();
                    g.clearMoveDialog();
                }
                return false;
            }
            attackSelector.addMovement(worldPosition);
            attackSelector.setAvailableAttackPosition(g.getMovementSelector(), getWorld());
            return true;
        }
        if (!setScopeEntity(worldPosition)) {
            attackSelector.reset();
            g.clearMoveDialog();
            return false;
        }
        if (getScopedEntity() instanceof Structure) return false;
        Pair<List<Pair<?, ?>>, List<int[]>> data = getTargetPossibilities();
        attackSelector.setOwner(getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction()));
        attackSelector.showValids(getScreen().getAssets(), data);
        attackSelector.setEntityInformation(worldPosition, getScopedEntity().getRange());
        return attackSelector.isOwner() && !(getScopedEntity() instanceof Villager);
    }

    private boolean isCurrentOwner(Pair<Integer, Integer> initialPosition) {
        boolean scoped = getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction());
        if (initialPosition == null) {
            return scoped;
        }
        Tile t = getTile(initialPosition.first, initialPosition.second);
        Faction faction = getModel().getCurrentPlayer().getFaction();
        return (t.entity.isPresent() && t.entity.get().getFaction().equals(faction)) && scoped;
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
        gameView.setAttack(false);
        gameView.getCursor().setLock(false);
        String path = selector.getPath();
        if (path.isBlank()) {
            gameView.clearSelectors();
            return;
        }
        Pair<Integer, Integer> destination = selector.getDestination();
        gameView.clearSelectors();
        if (gameView.isInAttackMode()) {
            actualiseFocusEntity(gameView.getAttackSelector().getInitialPosition());
        }
        getWorld().getScopedEntity().exhaust();
        getWorld().moveEntity(World.coordinatesToInt(destination, getWorld().getDimension()));
        Actor entity = gameView.getScopedEntity();
        if (entity instanceof CharacterUI) {
            ((CharacterUI) entity).setMove(path);

        }
    }

    /**
     * End the units attack selection, attack the units in the chosen emplacement and do the counterattack of the units attackted
     */
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
        getWorld().moveEntity(World.coordinatesToInt(position, getWorld().getDimension()));
        EntityUI actor = (EntityUI) gameView.getScopedEntity();
        Tile tile = getWorld().at(position);
        if (tile.entity.isEmpty()) return;
        actualiseFocusEntity(positionTarget);
        Entity entityTarget = getWorld().getScopedEntity();
        EntityUI actorTarget = (EntityUI) gameView.getCharacterUI(entityTarget);
        tile.entity.get().exhaust();
        boolean alive = attack((CharacterUI) actor,actorTarget, path);
        if (!commanderDie(entityTarget, tile.entity.get().getFaction())) {
            structureAttackted( actorTarget, tile.entity.get().getFaction());
            if (canCounterAttack( actorTarget, (CharacterUI) actor, alive)) {
                counterAttack( actorTarget,  actor, inversePath(path.substring(path.length() - 1)));
            }
        }
    }

    /**
     * @param path of the character
     * @return reverse the direction position of a the character
     */
    private String inversePath(String path) {
        String res = "";
        switch (path) {
            case "R":
                res = "L";
                break;
            case "L":
                res = "R";
                break;
            case "U":
                res = "D";
                break;
            case "D":
                res = "U";
                break;
            default:
        }
        return res;
    }

    /**
     * @param actor
     * @param actorTarget
     * @param path
     * @return
     */
    private boolean attack(CharacterUI actor, EntityUI actorTarget, String path) {
        actor.setMove(path.substring(0, path.length() - 1));
        actor.setAttackDirection(path.charAt(path.length() - 1));
        actor.setVictime(actorTarget);
        actor.getEntity().attack(actorTarget.getEntity());
        return actorTarget.getEntity().getHealth() > 0;
    }

    /**
     * When an entity is attacked, it can attack it aggressor
     *
     * @param actor       is the entity aggressed
     * @param actorTarget is the aggressor
     * @param path        of the aggressor
     */
    private void counterAttack(EntityUI actor, EntityUI actorTarget, String path) {
        actor.setReadyToAttack(false);
        if (actor instanceof CharacterUI) {
            CharacterUI characterUI = (CharacterUI) actor;
            characterUI.setAttackDirection(path.charAt(0));
        }
        actor.setVictime(actorTarget);
        actor.getEntity().attack(actorTarget.getEntity());
        commanderDie(actorTarget.getEntity(),actor.getEntity().getFaction());
    }

    /**
     * In case the Commander is dead, the player lost the game and he is remove of the game
     *
     * @param entityTarget is the commander
     */

    private boolean commanderDie(Entity entityTarget, Faction attack) {
        if (entityTarget instanceof Commander || entityTarget instanceof Stronghold) {
            if (entityTarget.getHealth() <= 0) {
                killArmyAndDestroyBases(getWorld().getPlayer(entityTarget.getFaction()), attack);
                Player defeated = getWorld().removePlayer(entityTarget.getFaction());
                gameOver(getModel().getWorld().getPlayer(attack), defeated);
                return true;
            }
        }
        return false;
    }

    /**
     * Only way to catch the StructureUI, is reading all the map
     * Eliminate or free all the entities of a player
     *
     * @param player
     * @param attack
     */
    private void killArmyAndDestroyBases(Player player, Faction attack) {
        Faction victim = player.getFaction();
        GameView gameView = (GameView) getScreen();
        Queue<Actor> delQueue = new LinkedList<>();
        gameView.getCharacters().getActors().forEach(actor -> {
            if ((actor instanceof CharacterUI) && ((CharacterUI) actor).getEntity().getFaction().equals(victim)) {
                delQueue.add(actor);
            }
        });
        while (!delQueue.isEmpty()) deleteCharacterUI((CharacterUI) delQueue.poll(), player);
        gameView.getStage().getActors().forEach(actor -> {
            if ((actor instanceof StructureUI) && (((StructureUI) actor).getEntity().getFaction().equals(victim))) {
                delQueue.add(actor);
            }
        });
        while (!delQueue.isEmpty()) deleteStructureUI((StructureUI) delQueue.poll(), player, attack);
    }

    private void structureAttackted(EntityUI entityUI, Faction attack) {
        Entity entity = entityUI.getEntity();
        if (entity instanceof Structure && entityUI instanceof StructureUI && entity.getHealth() <= 0) {
            entity.setHealth(1);
            if (entity.getFaction().equals(Faction.OUTLAWS)) {
                entity.setFaction(attack);
                getWorld().getPlayer(attack).addEntity(entity);
                entity.exhaust();
                getScreen().getStage().addActor(
                        new StructureUI(getScreen().getStage(), (Structure) entity, entityUI.getCoordinates())
                );
            } else {
                getWorld().getPlayer(entity.getFaction()).removeEntity(entity);
                entity.setFaction(Faction.OUTLAWS);
                getScreen().getStage().addActor(
                        new StructureUI(getScreen().getStage(), (Structure) entity, entityUI.getCoordinates())
                );
            }
            entityUI.remove();
        }
    }

    /**
     * Character is eliminated from the stage and world
     *
     * @param actor  is the character
     * @param player who lost his characters
     */
    private void deleteCharacterUI(CharacterUI actor, Player player) {
        Entity entity = actor.getEntity();
        getWorld().delEntity(actor.getCoordinates(), entity);
        player.removeEntity(entity);
        actor.remove();
    }

    /**
     * Structure change of owner, Stronghol and his entity are eliminated from the stage and world
     *
     * @param actor  is the structure
     * @param attack Faction of new owner
     * @param player who lost his structures
     */

    private void deleteStructureUI(StructureUI actor, Player player, Faction attack) {
        Entity entity = actor.getEntity();
        player.removeEntity(entity);
        if (entity instanceof Stronghold) {
            getWorld().delEntity(actor.getCoordinates(), entity);
        } else {
            entity.setFaction(Faction.OUTLAWS);
            getScreen().getStage().addActor(
                    new StructureUI(getScreen().getStage(), (Structure) entity, actor.getCoordinates())
            );
        }
        actor.remove();
    }

    /**
     * Shoes final dialog and allows to go to the main menu
     * @param winner winner
     * @param defeated The defeated faction
     */

    private void gameOver(Player winner, Player defeated) {
        if (getWorld().isTheLastPlayer()) {
            int seconds = 5;
            GameView gameView = (GameView) getScreen();
            Skin skin = gameView.getAssets().getSkin();
            Dialog dialog = new DialogWithCloseButton("", this) {
                @Override
                public void hide() {
                    super.hide();
                    stopGame();
                    openMainMenu();
                }
            };
            dialog.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    dialog.hide();
                    return true;
                }
            });
            Label timerText = new Label("Close in " + seconds + "seconds", skin);
            Label label = new Label(
                    "The " + winner.getFaction().prettyName()
                            + " won the war in " + getModel().getRound() + " rounds", skin
            );
            label.setColor(winner.getColor());
            timerText.setColor(Color.FIREBRICK);
            dialog.getContentTable().add(label).expand().fill();
            dialog.getContentTable().row();
            dialog.getContentTable().add(timerText);
            dialog.show(gameView.getGameViewUi());
            closeDialog(dialog, timerText, seconds);
        } else {
            showDefeatMessage(defeated);
        }
    }

    private void showDefeatMessage(Player defeated) {
        GameView g = (GameView) getScreen();
        Skin skin = Assets.getInstance().getSkin();
        int timer = 3;
        DialogWithCloseButton dialog = new DialogWithCloseButton("",this);
        Label label = new Label("The " + defeated.getFaction().prettyName() + " has been defeated !", skin);
        label.setColor(defeated.getColor());
        Label timerText = new Label("Close in "+ timer+ " seconds", skin);
        timerText.setColor(Color.FIREBRICK);
        dialog.getContentTable().add(label);
        dialog.getContentTable().row();
        dialog.getContentTable().add(timerText);
        dialog.show(g.getGameViewUi());
        closeDialog(dialog,timerText,timer);
    }

    public void actualiseFocusEntity(Pair<Integer, Integer> positionTarget) {
        if (positionTarget == null) return;
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
        if (gameView.isInAttackMode()) {
            actualiseFocusEntity(gameView.getAttackSelector().getInitialPosition());
        }
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

        if (s.isEmpty() || !(s.get() instanceof Recruitment)) {
            return;
        }
        gameView.getMovementSelector().setOwner(s.get().getFaction().equals(getModel().getCurrentPlayer().getFaction()));
        if (!gameView.getMovementSelector().isOwner()) return;
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
        GameView g = (GameView) getScreen();
        Vector3 v = g.getCursor().getWorldPosition();
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
        g.showsPlaceable(list);
        g.getMovementSelector().setOwner(s.get().getFaction().equals(getModel().getCurrentPlayer().getFaction()));
        g.getCursor().setLock(false);
    }

    /**
     * Place the bought entity stored in the model
     */
    public void placeBoughtEntity() {
        GameView gameView = (GameView) getScreen();
        gameView.getMovementSelector().reset();
        Vector3 v = gameView.getCursor().getWorldPosition();

        CharacterUI c = new CharacterUI(
                this, gameView.getCharacters(), new Pair<>((int) v.x, (int) v.y),
                (Character) getModel().getBoughtEntity()
        );
        gameView.getCharacters().addActor(c);
        getModel().getCurrentPlayer().addEntity(getModel().getBoughtEntity());
        getModel().getCurrentPlayer().buy(getModel().getBoughtEntity().getCost());
        getModel().getBoughtEntity().exhaust();
        getModel().getActiveStructure().exhaust();
        gameView.setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
        gameView.getCursor().setLock(false);
    }

    public void endTurn() {
        GameView gameView = ((GameView) getScreen());
        gameView.getCursor().setLock(false);
        gameView.clearAll();
        getModel().getCurrentPlayer().nextTurn();
        getModel().nextTurn();
        gameView.setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
        moveCameraToCommander();
        showNextTurnMessage();
    }

    private void moveCameraToCommander() {
        Queue<Entity> entities = getModel().getCurrentPlayer().getEntities();
        Entity commander = null;
        for (Entity e : entities) {
            if (e instanceof Commander) {
                commander = e;
                break;
            }
        }
        Actor commanderSprite = ((GameView) getScreen()).getCharacterUI(commander);
        if (commanderSprite == null) return;
        Camera camera = ((GameView) getScreen()).getCamera();
        camera.position.set(commanderSprite.getX(), commanderSprite.getY(), camera.position.z);
        ((GameView) getScreen()).getCursor().setPosition(commanderSprite.getX(), commanderSprite.getY());
    }

    public void nextUnit() {
        GameView gameView = (GameView) getScreen();
        gameView.clearAll();
        Entity e = getModel().getCurrentPlayer().nextPlayableEntity();
        if (e == null) {
            return;
        }
        Actor ui = gameView.getCharacterUI(e);
        if (ui == null) return;

        cameraDestination.first = ui.getX();
        cameraDestination.second = ui.getY();
        cameraMoving = true;
        gameView.getCursor().setPosition(ui.getX(), ui.getY());
    }

    public void actCamera(Camera camera) {
        float velocity = getClient().getCameraVelocity() * 64 * Gdx.graphics.getDeltaTime();
        boolean negX = cameraDestination.first - camera.position.x < 0;
        boolean negY = cameraDestination.second - camera.position.y < 0;
        float dx = (camera.position.x != cameraDestination.first) ? Model.getTileSize() : 0;
        float dy = (camera.position.y != cameraDestination.second) ? Model.getTileSize() : 0;
        dx *= (negX) ? -velocity : velocity;
        dy *= (negY) ? -velocity : velocity;
        camera.translate(dx, dy, 0);
        cameraMoving = (
                Math.abs(cameraDestination.first - camera.position.x) > Model.getTileSize() * 2
                        || Math.abs(cameraDestination.second - camera.position.y) > Model.getTileSize() * 2
        );
    }

    public boolean isCameraMoving() {
        return cameraMoving;
    }

    public void stopGame() {
        getModel().dispose();
    }

    public void openMainMenu() {
        getClient().stopMusic(true);
        setScreen(new MainMenu(this, getModel(), getClient()));
        getClient().setMusic(Assets.getInstance().get(Assets.AssetDir.SOUND.path() + "MENU.mp3"), true);
        getClient().playMusic();
    }

    public void closeClient() {
        Gdx.app.exit();
    }

    public void openInGameMenu() {
        setPrevious();
        View screen = new InGameMenu(getScreen(), this, getModel(), getClient());
        setScreen(screen);
    }

    public void changeCategory(String name, Database database, ScrollPane buttons) {
        database.selectCollection(name);
        Table content = ((SelectMap) getScreen()).initButtonsTable(database.getKeys());
        buttons.setActor(content);
    }


    public boolean haveARemainingUnit() {
        for (var e : getModel().getCurrentPlayer().getEntities()) {
            if (!e.isExhausted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param inLive if the actorTarget has survive to the actor atack
     * @return if the actorTarget can attack the actor
     */
    public boolean canCounterAttack(EntityUI actorTarget, CharacterUI actor, boolean inLive) {
        return inLive && !((actorTarget.getEntity()) instanceof Villager) && correctDistanceToCounterAttack(actorTarget, actor);
    }

    /**
     * @return true if the actor who has atackk first is in the range of the other actor
     * @return false if the actors are to futher
     */
    public boolean correctDistanceToCounterAttack(EntityUI characterUI, CharacterUI characterUIVictime) {
        Pair<Integer, Integer> posAttack = characterUI.getCoordinates();
        Pair<Integer, Integer> posVictime = characterUIVictime.calculateFinalPosition();
        int distanceX = Math.abs(posAttack.first - posVictime.first);
        int distanceY = Math.abs(posAttack.second - posVictime.second);
        int distance = Math.max(distanceX, distanceY);
        return distance <= characterUI.getEntity().getRange();
    }

    private void showNextTurnMessage() {
        Player current = getModel().getCurrentPlayer();
        Skin skin = Assets.getInstance().getSkin();
        int timer = 3;
        Dialog dialog = new DialogWithCloseButton("", this);
        dialog.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialog.hide();
                return true;
            }
        });
        Label label = new Label(current.getFaction().prettyName() + "'s turn to play", skin);
        label.setColor(current.getColor());
        Label timerText = new Label("Close in " + timer + "seconds", skin);
        timerText.setColor(Color.FIREBRICK);
        dialog.getContentTable().add(label).pad(10);
        dialog.getContentTable().row();
        dialog.getContentTable().add(timerText);
        dialog.show(((GameView) getScreen()).getGameViewUi());
        closeDialog(dialog, timerText, timer);
    }

    private void closeDialog(Dialog dialog, Label timerText, int seconds) {
        closeDialog(dialog, timerText, seconds, null);
    }

    private void closeDialog(Dialog dialog, Label label, int seconds, Runnable action) {
        if (label == null) {
            label = new Label("", Assets.getInstance().getSkin());
        }
        AtomicInteger timer = new AtomicInteger(seconds);
        Label timerText = label;
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                timerText.setText("Close in " + timer.getAndDecrement() + " seconds");
                if (timer.get() < 0) {
                    dialog.hide();
                    if (action != null) action.run();
                    this.cancel();
                }
            }
        }, 0, 1);
    }
}

