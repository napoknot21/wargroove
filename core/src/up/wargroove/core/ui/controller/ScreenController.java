package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * A basic screen controller.
 */
public abstract class ScreenController extends Controller {

    /**
     * The screen to control.
     */
    private Screen screen;

    public ScreenController(Game wargroove, Screen screen) {
        super(wargroove);
        this.screen = screen;
    }

    public ScreenController(Game wargroove) {
        super(wargroove);
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }
}
