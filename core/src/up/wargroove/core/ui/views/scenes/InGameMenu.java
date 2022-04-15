package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

public class InGameMenu extends View{
    View previous;
    /**
     * Initialize the screen.
     *
     * @param controller The screen controller.
     * @param model      The gui model.
     * @param wargroove  The client.
     */
    public InGameMenu(View screen, Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.previous = screen;
    }

    /**
     * Initialize the screen and its components.
     */
    @Override
    public void init() {
        Skin skin = Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "uiskin.json");
        setStage(new ScreenViewport());
        TextButton close = new TextButton("Return",skin);
        TextButton mainMenu = new TextButton("Main Menu", skin);
        TextButton quit = new TextButton("Quit", skin);
        TextButton settings = new TextButton("Settings", skin);
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
                getController().playSound(Assets.getInstance().getDefault(Sound.class));
                getController().back(previous);
            }
        });
        settings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(Assets.getInstance().getDefault(Sound.class));
                getController().openSettings();
            }
        });

        mainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(Assets.getInstance().getDefault(Sound.class));
                previous.dispose();
                getController().stopGame();
                getController().openMainMenu();
            }
        });

        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(Assets.getInstance().getDefault(Sound.class));
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
