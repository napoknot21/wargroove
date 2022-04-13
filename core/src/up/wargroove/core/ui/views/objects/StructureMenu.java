package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import up.wargroove.core.character.Entity;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

import java.util.List;
import java.util.Locale;

/**
 * Structures' menu where the player can buy characters.
 */
public class StructureMenu extends Dialog {
    private static StructureMenu instance;
    private final Description description;
    private final TextButton buy;
    private final Controller controller;
    private Entity current;

    private StructureMenu(Assets assets, Controller controller) {
        super("", assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class));
        this.description = new Description(assets);
        this.controller = controller;
        buy = new TextButton("buy", assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class));
        button("close", Buttons.CLOSE);
        button(buy, Buttons.BUY);
        buy.setVisible(false);
        setKeepWithinStage(false);
        setMovable(false);
        setModal(true);
    }

    /**
     * Shows the menu on the screen above all. All the inputs are catch by this.
     *
     * @param list list of purchasable characters.
     * @param assets     The app assets manager.
     * @param controller The app controller.
     * @param stage      The view stage.
     */
    public static void shows(List<Entity> list, Assets assets, Controller controller, Stage stage) {
        if (list == null) {
            return;
        }
        instance = new StructureMenu(assets, controller);
        instance.setup(list, assets);
        stage.getViewport().setScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        instance.show(stage);
    }

    /**
     * Transform the unit name into a displayable name.
     *
     * @param e the unit.
     * @return The displayable unit name.
     */
    private static String transformName(Entity e) {
        String s = e.getClass().getSimpleName().toLowerCase(Locale.ROOT) + " (" + e.getCost()+ ")";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * Build the menu.
     *
     * @param characters list of purchasable characters.
     * @param assets     The app assets manager.
     */
    private void setup(List<Entity> characters, Assets assets) {
        if (characters == null) {
            return;
        }
        Table buttons = new Table();
        characters.forEach(c -> buttons.add(new CharacterButton(c, assets)).row());
        ScrollPane pane = new ScrollPane(buttons);
        instance.getContentTable().add(pane);
        instance.getContentTable().add(instance.description).expand().fill();
    }

    @Override
    public float getPrefHeight() {
        return 200;
    }

    @Override
    public float getPrefWidth() {
        return 350;
    }

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    protected void result(Object object) {
        if (controller.isSoundOn()) {
            Assets.getInstance().getDefault(Sound.class).play();
        }
        if (object == null) {
            hide();
            return;
        }
        switch ((Buttons) object) {
            case BUY:
                buy();
                break;
            case CLOSE:
                close();
                break;
            default:
                break;
        }
    }

    /**
     * Buy the selected character.
     */
    private void buy() {
        if (current == null) {
            cancel();
            return;
        }
        controller.buy(current.getClass());
        close();
    }

    @Override
    public void keepWithinStage() {
        super.keepWithinStage();
    }

    /**
     * Close the menu.
     */
    private void close() {
        clear();
        controller.closeStructureMenu();
        getStage().getActors().removeValue(instance, true);
        instance = null;
    }

    public void setDebug(boolean debug) {
        super.setDebug(debug);
    }

    enum Buttons {
        CLOSE, BUY
    }

    /**
     * A character button is a button with the character name and its cost.
     */
    private class CharacterButton extends TextButton {
        Entity entity;

        public CharacterButton(Entity e, Assets assets) {
            super(transformName(e), assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class));
            entity = e;
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (controller.isSoundOn()) {
                        Assets.getInstance().getDefault(Sound.class).play();
                    }
                    description.setDescription(e, assets);
                    current = e;
                    buy.setVisible(true);
                }
            });
        }
    }

    /**
     * The description regroups the character' description text and its stats.
     */
    private class Description extends Table {
        private final Label text;
        private final Label movementCost;
        //private final Label attack;
        private final Label range;

        private Description(Assets assets) {
            Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
            text = new Label("", skin);
            movementCost = new Label("", skin);
            range = new Label("", skin);
            left();
            add(movementCost).left();
            row();
            add(range).left();
            row();
            add(new ScrollPane(text)).left();
            row();
            setVisible(false);
        }

        /**
         * Sets the description according to the given character.
         *  @param entity      The scoped character.
         * @param assets The app assets manager.
         */
        private void setDescription(Entity entity, Assets assets) {
            setVisible(true);
            movementCost.setText("Movement : "+entity.getMovement().id);
            range.setText("Range : "+entity.getRange());
            try {
                text.setText(assets.get(entity.getType(), 25));
            } catch (Exception e) {
                text.setText("Description unavailable");
            }
        }

        @Override
        public void clear() {
            text.clear();
            movementCost.clear();
        }
    }
}
