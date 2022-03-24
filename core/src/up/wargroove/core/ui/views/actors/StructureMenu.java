package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

import java.util.LinkedList;
import java.util.Locale;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class StructureMenu extends Dialog {
    private static StructureMenu instance;
    private final Description description;

    Controller controller;

    public StructureMenu(Assets assets, Controller controller) {
        super("",assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json",Skin.class));
        this.description = new Description(assets);
        this.controller = controller;
        button("close", Buttons.CLOSE);
        setKeepWithinStage(false);
        setMovable(false);
    }

    public static void shows(LinkedList<Character> characters, Assets assets, Controller controller, Stage stage) {
        instance = new StructureMenu(assets, controller);
        instance.setup(characters, assets);
        stage.getViewport().setScreenSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        instance.show(stage);
    }

    @Override
    public Dialog show(Stage stage) {
        show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.4f, Interpolation.fade)));
        int x = Math.round((stage.getWidth() + getWidth()) / 4);
        int y = Math.round((stage.getHeight() + getHeight()) / 4);
        setPosition(x,y);
        setPosition(getX() + x, getY() + y);
        return this;
    }

    private void setup(LinkedList<Character> characters, Assets assets) {
        Table buttons = new Table();
        characters.forEach(c -> buttons.add(new CharacterButton(c, assets)).pad(5).row());
        instance.getContentTable().add(buttons).height(buttons.getPrefHeight()).expandY().pad(10);
        instance.getContentTable().add(instance.description).height(buttons.getPrefHeight()).expandY().pad(10);
    }

    @Override
    public float getPrefHeight() {
        return 200;
    }

    @Override
    public float getPrefWidth() {
        return 200;
    }

    @Override
    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    @Override
    protected void result(Object object) {
        if (object == null) {
            hide();
            return;
        }
        switch ((Buttons) object) {
            case CLOSE:
                close();
                break;
            default:
                break;
        }
    }

    @Override
    public void keepWithinStage() {
        super.keepWithinStage();
    }

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

    private class CharacterButton extends TextButton {
        Character character;

        public CharacterButton(Character c, Assets assets) {
            super(
                    c.getType().name().toLowerCase(Locale.ROOT),
                    assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class)
            );
            character = c;
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    description.setDescription(c, assets);
                }
            });
        }
    }

    private class Description extends Table {
        private final Label text;
        private final Label cost;

        private Description(Assets assets) {
            Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
            text = new Label("", skin);
            cost = new Label("", skin);
            text.setWrap(true);
            add(cost);
            row();
            add(text);
            row();
            SpriteDrawable drawable = new SpriteDrawable(new Sprite(assets.getTest()));
            drawable.setMinSize(40, 20);

            pad(10);
            setVisible(false);
        }

        private void setDescription(Character c, Assets assets) {
            setVisible(true);
            cost.setText(Integer.toString(c.getCost()));
            try {
                text.setText(assets.get(c.getType(), 20));
            } catch (Exception e) {
                text.setText("Description unavailable");
            }
        }

        @Override
        public void clear() {
            text.clear();
            cost.clear();
        }
    }
}
