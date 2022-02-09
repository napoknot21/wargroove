package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.MainController;

/**
 * The Main Menu.
 */
public class MainMenu extends View {
    /**
     * Start game button.
     */
    private Button startGame;
    /**
     * World settings button
     */
    private Button worldSettings;
    /**
     * Screen controller.
     */
    private MainController controller;

    public MainMenu(MainController controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
    }

    public MainMenu(MainController controller, WargrooveClient wargroove) {
        this(controller, null, wargroove);
        this.controller = controller;
    }

    public MainMenu(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
        this.controller = new MainController(model, wargroove, this);
    }

    @Override
    public void init() {
        Table table = new Table();
        //table.top();
        //table.setFillParent(true);
        Skin skin = new Skin(Gdx.files.internal("data/gui/uiskin.json"));
        startGame = new TextButton("Start Game", skin);
        startGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.startGame();
                    }
                }
        );
        table.add(startGame).expandX();
        worldSettings = new TextButton("World Settings", skin);
        //table.add(worldSettings).expandX();
        addActor(table);
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void draw(float delta) {
        Batch batch = getBatch();
        batch.begin();
        getStage().draw();
        batch.end();
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
}
