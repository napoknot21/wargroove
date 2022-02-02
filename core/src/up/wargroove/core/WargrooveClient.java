package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.ClientController;
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
     * The app asset manager.
     */
    private AssetManager assetManager;
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
        assetManager = new AssetManager();
        Model model = new Model();
        controller = new ClientController(model, this);
        loadAssets();
        /*while(assetManager.update()) {
            float progress = assetManager.getProgress();
            System.out.println("Loading ... " + progress * 100 +"%");
        }*/
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

    /**
     * Loads assets in the assets' folder.
     */
    private void loadAssets() {
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
