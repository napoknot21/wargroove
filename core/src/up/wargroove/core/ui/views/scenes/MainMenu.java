package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

/**
 * The Main Menu.
 */
public class MainMenu extends View {
    /**
     * Start game button.
     */
    private Button startGame;

    /**
     * Player settings button.
     */
    private Button settings;

    private Button quit;
    /**
     * Screen controller.
     */
    private Controller controller;

    private Viewport viewport;

    private Sound buttonSound;

    public MainMenu(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.controller = controller;
    }

    public MainMenu(Controller controller, WargrooveClient wargroove) {
        this(controller, null, wargroove);
        this.controller = controller;
    }

    public MainMenu(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
        this.controller = new Controller(model, wargroove, this);
    }

    @Override
    public void init() {
        viewport = new ScreenViewport();
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = getAssets().getDefault(Sound.class);
        startGame = new TextButton("Start Game", skin);
        settings = new TextButton("Settings", skin);
        quit = new TextButton("Quit", skin);
        initListener();
        setStage(viewport);
        addActor(drawTable());
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void draw(float delta) {
        getStage().draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Init the table.
     *
     * @return The table.
     */
    private Table drawTable() {
        Table table = new Table();
        table.setFillParent(true);
        //table.top();
        table.add(startGame);
        table.row();
        table.add(settings);
        table.row();
        table.add(quit);

        return table;
    }

    /**
     * Init the buttons listener.
     */
    private void initListener() {
        startGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //makeSound(buttonSound);
                        if(controller.isSoundOn()) buttonSound.play();
                        controller.openMapSelection();
                    }
                }
        );


        settings.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //makeSound(buttonSound);
                        if(controller.isSoundOn()) buttonSound.play();
                        controller.openSettings();
                    }
                }
        );
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(controller.isSoundOn()){
                    buttonSound.play();
                }
                controller.closeClient();
            }
        });

    }
}
