package up.wargroove.core.ui.views.actors;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.character.Character;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.controller.Controller;

import java.util.LinkedList;
import java.util.Locale;

public class StructureMenu {
    private final Description description;
    private final Table menu;
    private InputMultiplexer input;
    private boolean active;

    public StructureMenu(Assets assets, Controller controller) {
        menu = new Table();
        menu.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
        menu.setColor(120,160,80,1);
        menu.setBackground(new SpriteDrawable(new Sprite(assets.get(Assets.AssetDir.WORLD.getPath() + "test.png", Texture.class))));
        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Stage stage = new Stage(viewport);;
        input =  new InputMultiplexer();
        stage.addActor(menu);
        this.description = new Description(assets, controller);
        active = false;
        input.addProcessor(stage);
    }

    public void shows(LinkedList<Character> characters, Assets assets) {
        Gdx.input.setInputProcessor(input);
        Table buttons = new Table();
        characters.forEach(c -> buttons.add(new CharacterButton(c, assets)).pad(5).row());
        menu.center();
        menu.add(buttons).expandY().pad(10);
        menu.add(description).expandY().pad(10);
        //menu.setFillParent(true);
        menu.getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        active = true;
    }

    public void setDebug(boolean debug) {
        menu.setDebug(debug);
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
                    description.setDescription(c);
                }
            });
        }
    }

    private class Description extends Table {
        private TextArea text;
        private TextField cost;
        private TextButton close;
        private Description(Assets assets, Controller controller) {
            Skin skin = assets.get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class);
            text = new TextArea("",skin);
            cost = new TextField("",skin);
            close = new TextButton("close",skin);
            add(cost);
            row();
            add(text);
            row();
            add(close);

            close.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    active = false;
                    menu.clear();
                    controller.closeStructureMenu();
                }
            });
        }
        private void setDescription(Character c) {
            cost.setText(Integer.toString(c.getCost()));
            text.setText("Unavailable for now");
        }
    }
    public void resize(int width, int height) {
        menu.getStage().getViewport().update(width,height);
    }

    public void draw() {
        if(!active) {
            return;
        }
        menu.getStage().draw();
    }
}
