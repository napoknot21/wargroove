package up.wargroove.core.ui;

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
        world = new int[50][50];
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
