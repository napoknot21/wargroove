package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;

/**
 * A basic screen controller.
 */
public abstract class ScreenController extends Controller {

    /**
     * The screen to control.
     */
    private Screen screen;

    public ScreenController(Model model, WargrooveClient wargroove, Screen screen) {
        super(model, wargroove);
        this.screen = screen;
    }

    public ScreenController(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
