package up.wargroove.core.ui;

import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.world.*;
import up.wargroove.utils.Pair;

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
     * Start a new game.
     */
    public void startGame() {
        if (world != null) {
            return;
        }
        if(properties == null){
            properties = new WorldProperties();
        }
        properties.dimension = new Pair<>(20, 20);
        properties.genProperties = new GeneratorProperties(3, -3.2, -12.0);
        world = new World(properties);

        Thread gen = new Thread(() -> world.initialize(true));
        gen.start();
        try {
            gen.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isActive = true;
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
        isActive = false;
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
}
