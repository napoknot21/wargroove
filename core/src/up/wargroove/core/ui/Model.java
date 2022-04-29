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

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * The gui model.
 */
public class Model {

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

    private Entity activeStructure = null;

    /**
     * Start a new game.
     */
    public void startGame() {
        if (isActive) {
            return;
        }
        if (world == null) {
            if (properties == null) {
                properties = new WorldProperties();
                properties.dimension = new Pair<>(20, 20);
                properties.genProperties = new GeneratorProperties(3, -3.2, -12.0);
            }
            world = new World(properties);

            Thread gen = new Thread(() -> world.initialize(true));
            gen.start();
            try {
                gen.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        world.loadPlayers();
        Recruitment.clearAll();
        EntityManager.getInstance().load();
        isActive = true;
        //addRandomStructure(); //for generate random structure
        setStartData();
    }

    private void addRandomStructure() {
        Random random = new Random();
        for (int i = 0; i < properties.getDimension().first; i++) {
            for (int j = 0; j < properties.getDimension().second; j++) {
                int rand = random.nextInt(100);
                if (rand < 10) {
                    Faction faction = (rand % 2 == 0) ? Faction.FELHEIM_LEGION : Faction.CHERRYSTONE_KINGDOM;
                    world.at(i, j).entity = Optional.of(new Recruitment(Recruitment.Type.BARRACKS, faction));
                }
            }
        }
    }

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
                                loadCommander(i, j, player);
                            }
                        }
                    }
                }
            }
        }
        System.out.println();
    }

    private void loadCommander(int i, int j, Player player) {
        List<Integer> adj = getWorld().adjacentOf(World.coordinatesToInt(new Pair<>(i,j),getWorld().getDimension()));
        adj.removeIf(pos -> {
            Tile t = getWorld().at(pos);
            return t.entity.isPresent() || !t.getType().isWalkable();
        });
        Random random = new Random();
        int pos = adj.get(random.nextInt(adj.size()));
        Entity entity = new Commander("Commander",player.getFaction());
        player.addEntity(entity);
        getWorld().at(pos).entity = Optional.of(entity);
        loadGuards(pos,player);
    }

    private void loadGuards(int pos, Player player) {
        List<Integer> adj = getWorld().adjacentOf(pos);
        adj.removeIf(i -> {
            Tile t = getWorld().at(i);
            return t.entity.isPresent() || !t.getType().isWalkable();
        });
        for (int i = 0; i< adj.size() && i < 2; i++) {
            Entity entity = new Soldier("Commander",player.getFaction());
            player.addEntity(entity);
            getWorld().at(adj.get(i)).entity = Optional.of(entity);
        }
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(WorldProperties properties) {
        this.world = (properties != null) ? new World(properties) : null;
        this.properties = properties;
    }

    public Biome getBiome() {
        return properties.getBiome();
    }

    public boolean isActive() {
        return isActive;
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

    public Entity getBoughtEntity() {
        return boughtEntity;
    }

    public void setBoughtEntity(Entity boughtEntity) {
        this.boughtEntity = boughtEntity;
    }

    public void nextTurn() {
        world.nextPlayer();
    }

    public Player getCurrentPlayer() {
        return world.getCurrentPlayer();
    }

    public int getRound() {
        return world.turns();
    }

    public void addPlayer(Faction faction) {
        world.addPlayer(faction);
    }

    public void removeLastPlayer() {
        world.removeLastPlayer();
    }

    @Null
    private Player getPlayer(Faction faction) {
        return world.getPlayer(faction);
    }

    public void endGame() {
    }

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
}
