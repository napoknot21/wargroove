package up.wargroove.core.ui.views.scenes;

import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.Controller;

/**
 * This is a view who manage a back buttons.
 */
public abstract class ViewWithPrevious extends View {
    /**
     * The previous screen.
     */
    private final View previous;

    /**
     * Constructor for screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public ViewWithPrevious(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.previous = previous;
    }

    /**
     * Getter for the previous view
     * @return the previous view
     */
    public View getPrevious() {
        return previous;
    }
}
