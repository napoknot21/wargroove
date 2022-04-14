package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.utils.Log;

/**
 * The Main Menu.
 */
public class MainMenu extends View {
    /**
     * Start game button.
     */
    private Button startGame;
    /**
     * Match settings button.
     */
    private Button matchSettings;
    /**
     * Player settings button.
     */
    private Button playerSettings;
    /**
     * Player settings button.
     */
    private Button mapSelection;

    private Button quit;
    /**
     * Screen controller.
     */
    private Controller controller;

    private Viewport viewport;

    private OrthographicCamera camera;

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
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = getAssets().getDefault(Sound.class);

        startGame = new TextButton("Start Game", skin);
        matchSettings = new TextButton("Match Settings", skin);
        playerSettings = new TextButton("Settings", skin);
        mapSelection = new TextButton("Choose Map", skin);
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
        table.add(matchSettings);
        table.row();
        table.add(mapSelection);
        table.row();
        table.add(playerSettings);
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
                        controller.startGame();
                    }
                }
        );

        mapSelection.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //makeSound(buttonSound);
                        if(controller.isSoundOn()) buttonSound.play();
                        controller.openMapSelection();
                    }
                }
        );
        playerSettings.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //makeSound(buttonSound);
                        if(controller.isSoundOn()) buttonSound.play();
                        controller.openSettings();
                    }
                }
        );
        matchSettings.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(controller.isSoundOn()) buttonSound.play();
                        controller.openMatchSettings();
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
