package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;

/**
 * The game controller.
 */
public class GameController extends ScreenController {
    public GameController(Model model, WargrooveClient wargroove, Screen screen) {
        super(model, wargroove, screen);
    }

    public GameController(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
    }
}
