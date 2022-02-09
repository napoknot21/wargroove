package up.wargroove.core;

import com.badlogic.gdx.Game;
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

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        Model model = new Model();
        controller = new Controller(model, this);
        controller.create();
        //scene = new GameView(controller.getModel(), this);
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
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() {
        return assets;
    }
}
