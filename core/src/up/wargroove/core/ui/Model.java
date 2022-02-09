package up.wargroove.core.ui;

import up.wargroove.core.ui.views.scenes.WorldSetting;
import up.wargroove.core.world.GeneratorProperties;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Pair;

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
     * Start a new game.
     */
    public void startGame() {
        properties = new WorldProperties();
        properties.dimension = new Pair<>(20,20);
        properties.genProperties = new GeneratorProperties();
        world = new World(properties);

        Thread gen = new Thread(() ->world.initialize(true));
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


}
