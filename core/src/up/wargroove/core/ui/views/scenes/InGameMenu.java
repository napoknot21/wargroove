package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

public class InGameMenu extends View{
    Screen previous;
    /**
     * Initialize the screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public InGameMenu(Screen screen, Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.previous = screen;
    }

    /**
     * Initialize the screen and its components.
     */
    @Override
    public void init() {
        Skin skin = Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "uiskin.json");
        setStage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        TextButton close = new TextButton("Return",skin);
        TextButton mainMenu = new TextButton("Main Menu", skin);
        TextButton quit = new TextButton("Quit", skin);
        TextButton settings = new TextButton("Settings", skin);
        close.setColor(Color.RED);
        Table table = new Table();
        table.setFillParent(true);
        table.add(close);
        table.row();
        table.add(settings);
        table.row();
        table.add(mainMenu);
        table.row();
        table.add(quit);
        addActor(table);

        close.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (getController().isSoundOn()) {
                    Assets.getInstance().getDefault(Sound.class).play();
                }
                getClient().setScreen(previous);
                dispose();
            }
        });
        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (getController().isSoundOn()) {
                    Assets.getInstance().getDefault(Sound.class).play();
                }
                getController().openSettings();
            }
        });

        mainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (getController().isSoundOn()) {
                    Assets.getInstance().getDefault(Sound.class).play();
                }
                previous.dispose();
                getController().stopGame();
                getController().openMainMenu();
            }
        });

        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (getController().isSoundOn()) {
                    Assets.getInstance().getDefault(Sound.class).play();
                }
                getController().closeClient();
            }
        });

    }

    /**
     * Draw the screen.
     *
     * @param delta The number of second that have passed since the last frame
     */
    @Override
    public void draw(float delta) {
        getStage().draw();
    }
}
