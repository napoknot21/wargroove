package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.scenes.GameView;

/**
 * The main menu controller.
 */
public class MainController extends ScreenController {

    public MainController(Model model, WargrooveClient wargroove, Screen screen) {
        super(model, wargroove, screen);
    }

    /**
     * Starts a new game.
     *
     */
    public void startGame() {
        Model model = getModel();
        this.getClient().setScreen(new GameView(model, getClient()));
    }
}
