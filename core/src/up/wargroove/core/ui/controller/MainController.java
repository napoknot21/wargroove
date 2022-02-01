package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import up.wargroove.core.ui.Model;

/**
 * The main menu controller.
 */
public class MainController extends ScreenController {

    public MainController(Game wargroove, Screen screen) {
        super(wargroove, screen);
    }

    /**
     * Starts a new game.
     *
     * @return true if everything works.
     */
    public boolean startGame() {
        Model model = getModel();
        //this.getClient().setScreen(new GameView(model));
        return true;
    }
}
