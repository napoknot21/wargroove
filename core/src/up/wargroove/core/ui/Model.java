package up.wargroove.core.ui;

import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.EntityManager;
import up.wargroove.core.character.Faction;
import up.wargroove.core.world.*;

import up.wargroove.core.character.Entity;
import up.wargroove.core.world.GeneratorProperties;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;

import up.wargroove.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * The gui model.
 */
public class Model {
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

    private int round;

    private ArrayList<Player> players;
    private int playerIndex;

    /**
     * Start a new game.
     */
    public void startGame() {
        if (world != null) {
            return;
        }
        if (properties == null) {
            properties = new WorldProperties();
        }
        properties.dimension = new Pair<>(20, 20);
        properties.genProperties = new GeneratorProperties(3, -3.2, -12.0);
        world = new World(properties);
        properties.setBiome(Biome.GRASS);

        Thread gen = new Thread(() -> world.initialize(true));
        gen.start();
        try {
            gen.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Recruitment.clearAll();
        EntityManager.getInstance().load();
        players = new ArrayList<>(4);
        isActive = true;
        round = 1;
        addPlayer(Faction.CHERRYSTONE_KINGDOM);
        addPlayer(Faction.FELHEIM_LEGION);
        addRandomStructure();
        setStartData();
    }

    private void addRandomStructure() {
        Random random = new Random();
        for (int i = 0; i < properties.getDimension().first; i++) {
            for (int j = 0; j<properties.getDimension().second; j++) {
                int rand = random.nextInt(100);
                if (rand < 10) {
                    Faction faction = (rand %2 == 0)? Faction.FELHEIM_LEGION : Faction.CHERRYSTONE_KINGDOM;
                    world.at(i,j).entity = Optional.of(new Recruitment(Recruitment.Type.BARRACKS, faction));
                }
            }
        }
    }

    private void setStartData() {
        for (int i = 0; i < properties.getDimension().first; i++) {
            for (int j = 0; j<properties.getDimension().second; j++) {
                if (world.at(i,j).entity.isPresent()) {
                    Entity entity = world.at(i,j).entity.get();
                    Player player = getPlayer(entity.getFaction());
                    if (player != null) {
                        player.addEntity(entity);
                    }
                }
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public void setProperties(WorldProperties properties) {
        this.properties = properties;
    }

    public Biome getBiome(){
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
        playerIndex = 0;
        isActive = false;
        players.clear();
        boughtEntity = null;
        round = 1;
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
        playerIndex = (playerIndex + 1) % players.size();
        round += (playerIndex == 0)? 1 : 0;
    }
    public Player getCurrentPlayer() {
        return players.get(playerIndex);
    }

    public int getRound() {
        return round;
    }

    public void addPlayer(Faction faction) {
        for (Player p : players) {
            if (p.getFaction().equals(faction)) {
                return;
            }
        }
        Player p = new Player(faction);
        players.add(p);
        p.setName("Player " + players.size());
    }

    public void removeLastPlayer() {
        if (players.isEmpty()) {
            return;
        }
        players.remove(players.size() - 1);
    }

    @Null
    private Player  getPlayer(Faction faction) {
        for (Player player : players) {
            if (player.getFaction().equals(faction)) {
                return player;
            }
        }
        return null;
    }

    public void endGame() {
    }
}
