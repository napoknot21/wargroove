package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

/**
 * The current player information box on screen.
 */
public class PlayerBox extends Table {
    /**
     * Indicate if this box is for a unique player
     */
    private final boolean isStatic = false;

    /**
     * The current player's name.
     */
    private final Label name;

    /**
     * The current player's amount of money.
     */
    private final Label money;

    /**
     * The current player's.
     */
    private final Label income;

    /**
     * The game's round.
     */
    Label round;

    /**
     * The current player's avatar.
     */
    Image avatar;

    /**
     * Inits an empty player box.
     *
     * @param controller not used for now.
     */
    public PlayerBox(Controller controller) {
        Skin skin = Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
        name = new Label("Friendly", skin);
        money = new Label("300", skin);
        income = new Label("+500", skin);
        round = new Label("Round 1", skin);
        avatar = new Image(Assets.getInstance().getTest());
        setup();
    }

    /**
     * builds the playerBox and manage its elements.
     */
    private void setup() {
        Table table = new Table();
        table.left().top();
        table.add(name).expandX().fillX().pad(0, 0, -5, 0);
        table.row();
        table.add(money).expandX().fillX().pad(0, 0, -10, 0);
        table.row();
        table.add(income).expandX().fillX().pad(0, 0, -5, 0);
        table.row();
        table.add(round).expandX().fillX();
        add(table).expand().pad(10);
        add(avatar).size(70);
    }

    /**
     * Sets the playerBox displayed information.
     *
     * @param o The current player
     */
    public void setInformations(Object o) {
        if (isStatic) {
            return;
        }
    }
}
