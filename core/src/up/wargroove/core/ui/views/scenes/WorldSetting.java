package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import up.wargroove.core.ui.controller.Controller;


public class WorldSetting extends View{

    private Stage ui;
    private Viewport viewport;
    private Button select;
    private static int HEIGHT = 480;
    private static int WIDTH = 600;


    public WorldSetting(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        viewport = new FitViewport(WIDTH, HEIGHT, new OrthographicCamera());
        ui = new Stage(viewport);
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        Skin skin = new Skin(Gdx.files.internal("data/gui/uiskin.json"));
        select = new TextButton("Choisir",skin);
        table.add(select);
    }




    @Override
    public void init() {

    }

    @Override
    public void draw(float delta) {

    }
}
