package up.wargroove.core.ui;

import java.util.Random;

/**
 * The gui model.
 */
public class Model {
    /**
     * The world.
     */
    private int[][] world;
    /**
     * Indicate if the model is used by the gui.
     */
    private boolean isActive = false;

    /**
     * Start a new game.
     */
    public void startGame() {
        world = new int[500][500];
        Random random = new Random();
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                world[i][j] = random.nextInt(13) + 1;
            }
        }
        isActive = true;
    }

    public int[][] getWorld() {
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
