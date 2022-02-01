package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
     * Screen controller.
     */
    private MainController controller;

    public MainMenu(MainController controller, Model model, Game wargroove) {
        super(controller, model, wargroove);
    }

    public MainMenu(MainController controller, Game wargroove) {
        this(controller, null, wargroove);
        this.controller = controller;
    }

    @Override
    public void init() {
        Table table = new Table();
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("default/uiskin.atlas"));
        Skin skin = new Skin(atlas);
        startGame = new TextButton("Start Game", skin);
        startGame.addListener((e) -> controller.startGame());
        table.add(startGame);
        addActor(table);
    }

    @Override
    public void draw(float delta) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}
