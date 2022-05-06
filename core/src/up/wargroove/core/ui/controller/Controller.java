package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Null;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.character.Character;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.entities.Commander;
import up.wargroove.core.character.entities.Villager;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.objects.*;
import up.wargroove.core.ui.views.scenes.*;
import up.wargroove.core.world.*;
import up.wargroove.utils.Database;
import up.wargroove.utils.Pair;

import java.util.List;
import java.util.*;


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
        float max = (camera.viewportHeight + camera.viewportWidth) / 1.5f + 10;
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
        camera.translate(
                -Gdx.input.getDeltaX(pointer) * velocity, Gdx.input.getDeltaY(pointer) * velocity
        );
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
     * @param movementSelector The screen movement manager.
     * @param worldPosition    The position in world coordinates.
     * @return true if the movements must be drawn false otherwise.
     */
    public boolean showMovements(boolean movement, MovementSelector movementSelector, Vector3 worldPosition) {
        GameView g = (GameView) getScreen();
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
        if (!getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction())
                || getScopedEntity() instanceof Structure) {
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
            if (!attackSelector.isValidPosition(worldPosition)) {
                attackSelector.reset();
                g.clearMoveDialog();
                return false;
            }
            return false;
        }
        if (!setScopeEntity(worldPosition)) {
            attackSelector.reset();
            g.clearMoveDialog();
            return false;
        }
        if (!getScopedEntity().getFaction().equals(getModel().getCurrentPlayer().getFaction())
                || getScopedEntity() instanceof Villager) {
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
        Actor actor = gameView.getScopedEntity();
        Tile tile = getWorld().at(position);
        if (tile.entity.isEmpty()) return;
        actualiseFocusEntity(positionTarget);
        Entity entityTarget = getWorld().getScopedEntity();
        Actor actorTarget = gameView.getCharacterUI(entityTarget);
        tile.entity.get().exhaust();
        boolean inLive = attack((CharacterUI) actor, (EntityUI) actorTarget, path);
        if (!commanderDie(entityTarget, tile.entity.get().getFaction())) {
            structureAttackted((EntityUI) actorTarget, tile.entity.get().getFaction());
            if (canCounterAttack((EntityUI) actorTarget, (CharacterUI) actor,inLive)){
                contreAttack((CharacterUI)actorTarget,(CharacterUI) actor,inversePath(path));
            }
        }
    }

    private String inversePath(String path) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            switch (path.charAt(i)) {
                case 'R':res.append('L');break;
                case 'L':res.append('R'); break;
                case 'U':res.append('D');break;
                case'D':res.append('U'); break;
                default:
            }
        }
        return res.toString();
    }

    private boolean attack(CharacterUI actor, EntityUI actorTarget, String path) {
        actor.setMove(path.substring(0, path.length() - 1));
        actor.setAttackDirection(path.charAt(path.length() - 1));
        actor.setVictime(actorTarget);
        actor.getEntity().attack(actorTarget.getEntity());
        return actorTarget.getEntity().getHealth()>0;
    }

    private void contreAttack(CharacterUI actor, EntityUI actorTarget, String path) {
        actor.setReadyToAttack(false);
        actor.setAttackDirection(path.charAt(path.length() - 1));
        actor.setVictime(actorTarget);
        actor.getEntity().attack(actorTarget.getEntity());
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
                getWorld().removePlayer(entityTarget.getFaction());
                gameOver(attack);
                return true;
            }
        }
        return false;
    }

    /**
     * Only way to catch the StructureUI, is reading all the map
     *
     * @param player
     * @param enemie
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
            if (entity.getFaction().equals(Faction.OUTLAWS)){
                entity.setFaction(attack);
                getWorld().getPlayer(attack).addEntity(entity);
                entity.exhaust();
                getScreen().getStage().addActor(
                        new StructureUI(getScreen().getStage(), (Structure) entity, entityUI.getCoordinates())
                );
            } else {
                getWorld().getPlayer(entity.getFaction()).removeEntity(entity);
                entity.setFaction(Faction.OUTLAWS);
                getWorld().getFantome().addEntity(entity);
                entity.setHealth(20);
                getScreen().getStage().addActor(
                        new StructureUI(getScreen().getStage(), (Structure) entity, entityUI.getCoordinates())
                );
            }
            entityUI.remove();
        }
    }

    private void deleteCharacterUI(CharacterUI actor, Player player) {
        Entity entity = actor.getEntity();
        getWorld().delEntity(actor.getCoordinates(), entity);
        player.removeEntity(entity);
        actor.remove();
    }

    private void deleteStructureUI(StructureUI actor, Player player, Faction attack) {
        Entity entity = actor.getEntity();
        player.removeEntity(entity);
        Player rival = getWorld().getPlayer(attack);
        if (entity instanceof Stronghold) {
            getWorld().delEntity(actor.getCoordinates(), entity);
        } else {
            entity.setFaction(Faction.OUTLAWS);
            getWorld().getFantome().addEntity(entity);
            getScreen().getStage().addActor(
                    new StructureUI(getScreen().getStage(), (Structure) entity, actor.getCoordinates())
            );
        }
        actor.remove();
    }

    private void gameOver(Faction faction) {
        if (getWorld().isTheLastPlayer()) {
            GameView gameView = (GameView) getScreen();
            Skin skin = gameView.getAssets().getSkin();
            Dialog dialog = new Dialog("", gameView.getAssets().getSkin());
            Label label = new Label(
                    "The " + faction.prettyName() + " won the war in " + getModel().getRound() + " rounds", skin
            );
            label.setColor(Color.BLACK);
            TextButton button = new TextButton("Close", skin);
            dialog.getContentTable().add(label).expand().fill();
            dialog.button(button);
            dialog.show(gameView.getGameViewUi());
            Controller controller = this;
            button.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            controller.playSound(Assets.getInstance().getDefault(Sound.class));
                            controller.stopGame();
                            controller.openMainMenu();
                        }
                    });
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

                return false;
            }
        }
        return false;
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
        ((GameView) getScreen()).getCursor().setLock(false);
        ((GameView) getScreen()).clearAll();
        getModel().getCurrentPlayer().nextTurn();
        getModel().nextTurn();
        ((GameView) getScreen()).setPlayerBoxInformations(getModel().getCurrentPlayer(), getModel().getRound());
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
                Math.abs(cameraDestination.first - camera.position.x) > Model.getTileSize()
                        || Math.abs(cameraDestination.second - camera.position.y) > Model.getTileSize()
        );
        if (!cameraMoving) {
            camera.position.set(cameraDestination.first, cameraDestination.second, camera.position.z);
        }
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

    public boolean canCounterAttack(EntityUI actorTarget, CharacterUI actor , boolean inLive){
        return((actorTarget instanceof CharacterUI) && inLive && !(((CharacterUI) actorTarget).getEntity() instanceof Villager) && canFit((CharacterUI) actorTarget, actor));

        }

    public boolean canFit(CharacterUI characterUI, CharacterUI characterUIVictime){
        Pair<Integer,Integer> posAttack= characterUI.getCoordinates();
        Pair<Integer,Integer> posVictime= characterUIVictime.getCoordinates();
        int distanceX = Math.abs(posAttack.first - posVictime.first);
        int distanceY = Math.abs(posAttack.second -posVictime.second);
        int distance = Math.max(distanceX,distanceY);
        return distance<= characterUI.getEntity().getRange();

    }
}
