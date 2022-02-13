package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
     * World settings button.
     */
    private Button worldSettings;
    /**
     * Screen controller.
     */
    private Controller controller;

    private Viewport viewport;

    private OrthographicCamera camera;

    public MainMenu(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
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
        startGame = new TextButton("Start Game", skin);
        worldSettings = new TextButton("World Settings", skin);
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
        table.add(worldSettings);
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
                        controller.startGame();
                    }
                }
        );

        worldSettings.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Log.print("World Settings");
                    }
                }
        );
    }
}
