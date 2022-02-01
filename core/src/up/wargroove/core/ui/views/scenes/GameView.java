package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Game;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.GameController;
import up.wargroove.core.ui.controller.ScreenController;
import up.wargroove.core.ui.views.objects.GameMap;

/**
 * Represent the game screen.
 */
public class GameView extends View {
    /**
     * Visual of the world.
     */
    private GameMap gameMap;

    public GameView(ScreenController controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
    }

    public GameView(Model model, WargrooveClient wargroove) {
        this(new GameController(model, wargroove), model, wargroove);
        getController().setScreen(this);
    }

    @Override
    public void init() {
        getModel().startGame();
        gameMap = new GameMap(getModel().getWorld());
        gameMap.render();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void draw(float delta) {
        gameMap.render();
    }

    @Override
    public void dispose() {
        gameMap = null;
        super.dispose();
    }
}
