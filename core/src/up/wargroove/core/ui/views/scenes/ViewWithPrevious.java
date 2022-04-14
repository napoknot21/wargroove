package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Screen;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

public abstract class ViewWithPrevious extends View{
    private final View previous;
    /**
     * Initialize the screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public ViewWithPrevious(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.previous = previous;
    }

    public View getPrevious() {
        return previous;
    }
}
