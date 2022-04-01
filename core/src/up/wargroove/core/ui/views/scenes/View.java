package up.wargroove.core.ui.views.scenes;


import com.badlogic.gdx.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

/**
 * Represent a basic screen.
 *
 * @see com.badlogic.gdx.Screen
 */
public abstract class View extends ScreenAdapter {

    /**
     * Screen controller.
     */
    private final Controller controller;
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

    //final Sound BUTTON_SOUND;

    /**
     * The Screen input manager.
     */
    private final InputMultiplexer inputs;


    /**
     * Initialize the screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public View(Controller controller, Model model, WargrooveClient wargroove) {
        this.controller = controller;
        this.model = model;
        this.ui = new Stage();
        this.wargroove = wargroove;

        inputs = new InputMultiplexer();

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
        inputs.addProcessor(ui);
        Gdx.input.setInputProcessor(inputs);
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

    public Controller getController() {
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

    /**
     * Sets the main stage of the view.
     *
     * @param viewport The stage's viewport.
     * @return the created stage.
     */
    public Stage setStage(Viewport viewport) {
        this.ui = new Stage(viewport, getBatch());
        inputs.addProcessor(ui);
        return ui;
    }

    public Assets getAssets() {
        return wargroove.getAssets();
    }


    public void makeSound(Sound s) {
        if (controller.isSoundOn()) {
            s.play();
        }
    }

    public void makeMusic(Music m){
        if(controller.isSoundOn()){
            m.setVolume(0.5f);
            //m.setLooping(true);
            m.play();
        }
    }


    /**
     * Puts the view in debug mode.
     *
     * @param debug if true, the app is in debug mode
     */
    public void setDebug(boolean debug) {
        ui.setDebugAll(debug);
    }


    public InputMultiplexer getInputs() {
        return inputs;
    }

    public void addInput(InputProcessor input) {
        inputs.addProcessor(input);
    }

}
