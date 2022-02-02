package up.wargroove.core.ui.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;

/**
 * The game controller.
 */
public class GameController extends ScreenController {

    /**
     * Camera velocity.
     */
    private static float sVelocity = 0.30f;

    public GameController(Model model, WargrooveClient wargroove, Screen screen) {
        super(model, wargroove, screen);
    }

    public GameController(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
    }

    /**
     * Do a camera zoom according to the mouse wheel.
     * <b>Mouse only</b>
     *
     * @param amountX Is ignored
     * @param amountY The mouse wheel axis.
     * @param camera  The screen camera.
     */
    public void zoom(float amountX, float amountY, OrthographicCamera camera) {
        camera.zoom += amountY * Gdx.graphics.getDeltaTime();
        camera.update();
    }

    /**
     * Drag the camera according to the user input and the configured camera velocity in the settings.
     *
     * @param pointer The pointer for the event.
     * @param camera The screen camera.
     */
    public void drag(int pointer, OrthographicCamera camera) {
        float velocity = sVelocity * 50 * Gdx.graphics.getDeltaTime();
        camera.translate(
                -Gdx.input.getDeltaX(pointer) * velocity, Gdx.input.getDeltaY(pointer) * velocity
        );
        camera.update();
    }

    /**
     * Set the visual setting of the controller.
     */
    private void setSetting() {
        sVelocity = 0;
    }
}
