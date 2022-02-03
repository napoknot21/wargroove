package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.ScreenController;

/**
 * Represent a basic screen.
 *
 * @see com.badlogic.gdx.Screen
 */
public abstract class View extends ScreenAdapter {

    /**
     * Screen controller.
     */
    private final ScreenController controller;
    /**
     * UI model.
     */
    private final Model model;
    /**
     * The client.
     */
    private final WargrooveClient wargroove;
    /**
     * The main stage of the screen.
     */
    private Stage ui;


    /**
     * Initialize the screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public View(ScreenController controller, Model model, WargrooveClient wargroove) {
        this.controller = controller;
        this.model = model;
        this.ui = new Stage(new ScreenViewport());
        this.wargroove = wargroove;
    }

    /**
     * Initialize a screen without a controller. <br>
     * <b> A Controller must be set later.</b>
     *
     * @param model     The gui model.
     * @param wargroove The client.
     */
    public View(Model model, WargrooveClient wargroove) {
        this(null, model, wargroove);
    }

    @Override
    public void show() {
        InputMultiplexer input = new InputMultiplexer();
        input.addProcessor(ui);
        Gdx.input.setInputProcessor(input);
        init();
    }

    /**
     * Initialize the screen and its components.
     */
    public abstract void init();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        draw(delta);
        super.render(delta);
    }

    /**
     * Draw the screen.
     *
     * @param delta The number of second that have passed since the last frame
     */
    public abstract void draw(float delta);

    @Override
    public void resize(int width, int height) {
        ui.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        if (ui != null) {
            ui.dispose();
        }
        ui = null;
    }

    public Model getModel() {
        return model;
    }

    public ScreenController getController() {
        return controller;
    }

    /**
     * Adds an actor to the stage.
     *
     * @param actor the actor that will be added
     */
    public void addActor(Actor actor) {
        ui.addActor(actor);
    }

    public WargrooveClient getClient() {
        return wargroove;
    }

    public Batch getBatch() {
        return wargroove.getBatch();
    }

    public Stage getStage() {
        return ui;
    }

    public Assets getAssets() {
        return wargroove.getAssets();
    }
}
