package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;

/**
 * A basic gui controller.
 */
public abstract class Controller {
    /**
     * The wargroove client.
     */
    private final WargrooveClient wargroove;
    /**
     * The game model.
     */
    private Model model;

    public Controller(Model model, WargrooveClient wargroove) {
        this.wargroove = wargroove;
        this.model = model;
    }

    /**
     * Create a model.
     */
    public void create() {
        model = new Model();
    }

    public Model getModel() {
        return model;
    }


    public WargrooveClient getClient() {
        return wargroove;
    }
}
