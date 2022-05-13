package up.wargroove.core.ui;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.EntityManager;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.entities.Commander;
import up.wargroove.core.character.entities.Soldier;
import up.wargroove.core.world.*;
import up.wargroove.utils.Pair;

import java.util.*;

/**
 * The gui model.
 */
public class Model {

    /**
     * The map tile size.
     */
    private static int tileSize;
    /**
     * The world.
     */
    private World world;
    /**
     * The world's properties.
     */
    private WorldProperties properties;

    /**
     * Indicate if the model is used by the gui.
     */
    private boolean isActive = false;

    /**
     * Bought entity temporary storage.
     */
    private Entity boughtEntity = null;

    /**
     * Structure that bought the entity.
     */
    private Entity activeStructure = null;



    /**
     * Start a new game.
     */
    public void startGame() {
        if (isActive) {
            return;
        }
        world.loadPlayers();
        Recruitment.clearAll();
        EntityManager.getInstance().load();
        isActive = true;
        updateTiles();
        setStartData();
        System.out.println(getBiome().name());
    }

    private void updateTiles() {
        for (Tile t : world.getTerrain()) {
            t.updateType(getBiome());
        }
    }

    /**
     * Initialize the players' data according to the World.
     */
    private void setStartData() {
        for (int i = 0; i < properties.getDimension().first; i++) {
            for (int j = 0; j < properties.getDimension().second; j++) {
                if (world.at(i, j).entity.isPresent()) {
                    Entity entity = world.at(i, j).entity.get();
                    if ((entity instanceof Structure)) {
                        Player player = getPlayer(entity.getFaction());
                        if (player != null) {
                            player.addEntity(entity);
                            if (entity instanceof Stronghold) {
                                loadTroops(i, j, player);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Load the commander at an adjacent tile to the tile (i,j) which contains the stronghold.
     * Calls to loadGuards() in order to instantiate the close-knit guards.
     *
     * @param i      The tile first coordinate
     * @param j      The tile second coordinate
     * @param player The owner of the stronghold
     */
    private void loadTroops(int i, int j, Player player) {
        int pos = World.coordinatesToInt(new Pair<>(i, j), getWorld().getDimension());
        int commanderPos = troopGenerationBfs(1, pos,new Commander("Commander", player.getFaction()), player);
        troopGenerationBfs(2,commanderPos,new Soldier("Knit-Guards", player.getFaction()), player);
    }


    private int troopGenerationBfs(int amt, int pos, Entity cloneGen, Player player) {
        HashSet<Integer> checked = new HashSet<>();
        Queue<Integer> emp = new LinkedList<>();
        emp.add(pos);
        int ret = 0;
        while(amt > 0 && !emp.isEmpty()) {
            int el = emp.poll();
            List<Integer> adj = getWorld().adjacentOf(el);
            for (int i = 0; i< adj.size() && amt > 0; i++) {
                if (checked.contains(adj.get(i))) continue;
                Tile t = getWorld().at(adj.get(i));
                if (t.entity.isEmpty() && t.getType().availableForLoad()) {
                    try {
                        Entity entity = (Entity) cloneGen.clone();
                        player.addEntity(entity);
                        getWorld().at(adj.get(i)).entity = Optional.of(entity);
                        amt--;
                        ret = adj.get(i);
                    } catch (CloneNotSupportedException ignored) {
                    }
                }
                checked.add(adj.get(i));
                emp.add(adj.get(i));
            }
        }
        return ret;
    }


    /**
     * Destroy the model.
     */
    public void dispose() {
        world = null;
        properties = null;
        isActive = false;
        boughtEntity = null;
    }

    /**
     * Gets the tile at the given vector.
     *
     * @param vector The tile position.
     * @return The tile indicated by the vector.
     */
    public Tile getTile(Vector3 vector) {

        int x = (vector.x < 0) ? 0 : (int) Math.min(world.getDimension().first - 1, vector.x);
        int y = (vector.y < 0) ? 0 : (int) Math.min(world.getDimension().second - 1, vector.y);
        return world.at(x, y);
    }

    /**
     * Gets the tile at the given vector.
     *
     * @param x The tile x position.
     * @param y The tile y position.
     * @return The tile indicated by the vector.
     */
    public Tile getTile(int x, int y) {

        x = (x < 0) ? 0 : Math.min(world.getDimension().first - 1, x);
        y = (y < 0) ? 0 : Math.min(world.getDimension().second - 1, y);
        return world.at(x, y);
    }

    /**
     * Change the current player.
     */
    public void nextTurn() {
        world.nextPlayer();
    }

    /**
     * Gets the current player.
     *
     * @return return the player.
     */
    public Player getCurrentPlayer() {
        return world.getCurrentPlayer();
    }

    /**
     * Gets the round number.
     *
     * @return the round number.
     */
    public int getRound() {
        return world.turns();
    }

    /**
     * Add a player to the list
     *
     * @param faction The player's faction.
     */
    public void addPlayer(Faction faction) {
        world.addPlayer(faction);
    }

    /**
     * Remove the last entered player.
     */
    public void removeLastPlayer() {
        world.removeLastPlayer();
    }

    public void endGame() {}

    /***************** setters and getters *****************/

    public Entity getActiveStructure() {
        return activeStructure;
    }

    public void setActiveStructure(Entity activeStructure) {
        this.activeStructure = activeStructure;
    }

    public WorldProperties getProperties() {
        return properties;
    }

    public void setProperties(WorldProperties properties) {
        this.properties = properties;
    }

    public static int getTileSize() {
        return tileSize;
    }

    public static void setTileSize(int tileSize) {
        Model.tileSize = tileSize;
    }

    /**
     * Gets the faction's player.
     *
     * @param faction the requested faction.
     * @return the player if its exist, null otherwise.
     */
    @Null
    private Player getPlayer(Faction faction) {
        return world.getPlayer(faction);
    }

    public Entity getBoughtEntity() {
        return boughtEntity;
    }

    public void setBoughtEntity(Entity boughtEntity) {
        this.boughtEntity = boughtEntity;
    }

    /**
     * Gets the world biome.
     * @return The biome.
     */
    public Biome getBiome() {
        return properties.getBiome();
    }

    public boolean isActive() {
        return isActive;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(WorldProperties properties) {
        this.world = (properties != null) ? new World(properties) : null;
        this.properties = properties;
    }

}
