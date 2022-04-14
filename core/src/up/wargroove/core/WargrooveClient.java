package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.scenes.MainMenu;
import up.wargroove.core.ui.views.scenes.View;

/**
 * The wargroove client.
 */
public class WargrooveClient extends Game {

    public WargrooveClient(boolean debug) {
        this.debug = debug;
    }

    public WargrooveClient() {
        this(false);
    }

    /**
     * The drawing tool.
     */
    SpriteBatch batch;
    /**
     * The app assets.
     */
    private Assets assets;
    /**
     * The client controller.
     */
    private Controller controller;
    /**
     * The shown scene.
     */
    private View scene;

    /**
     * Indicate if the client is in debug mode.
     */
    private boolean debug;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = Assets.getInstance();
        assets.loadDefault();
        assets.loadEntitiesDescription();
        Model model = new Model();
        controller = new Controller(model, this);
        controller.create();
        scene = new MainMenu(controller.getModel(), this);
        setScreen(scene);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        scene.dispose();
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void render() {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() {
        return assets;
    }

    /**
     * Puts the app in debug mode.
     *
     * @param debug if true, the app is in debug mod
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (scene != null) {
            scene.setDebug(debug);
        }
    }

    /**
     * Keeps the debug mode if it was already activated.
     */
    public void setDebug() {
        setDebug(debug);
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        this.scene = (View) screen;
        setDebug();
    }
}
