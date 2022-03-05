package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

import java.util.ArrayList;

/**
 * The World Settings Menu.
 */
public class MatchSettings extends View {
    /**
     * Previous screen button.
     */
    private Button back;
    /**
     * Button to reset the settings to the defaults values
     */
    private Button reset;
    /**
     * Screen controller.
     */
    private Controller controller;
    /**
     * the viewport
     */
    private Viewport viewport;
    /**
     * the camera
     */
    private OrthographicCamera camera;
    /**
     * The sound of the buttons
     */
    private Sound buttonSound;
    /**
     * label of the weather SelectBox
     */
    private Label weatherLabel;
    /**
     * label of the timer SelectBox
     */
    private Label turnTimeLabel;
    /**
     * label of the fog SelectBox
     */
    private Label fogLabel;
    private Label incomeLabel;
    private Label printIncome;
    private Label biomeLabel;
    private Label commandersLabel;
    private Label teamLabel;
    /**
     * Weather SelectBox
     */
    private SelectBox weather;
    private SelectBox turnTime;
    private SelectBox fog;
    private Slider income;
    private SelectBox biome;
    private SelectBox commanders;
    private SelectBox team;

    private Skin skin;




    private int HEIGHT = Gdx.graphics.getHeight();
    private int WIDTH = Gdx.graphics.getWidth();

    public MatchSettings(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        this.controller = controller;
        this.skin = getAssets().getDefault(Skin.class);

    }

    public MatchSettings(Controller controller, WargrooveClient wargroove) {
        this(controller, null, wargroove);
        this.controller = controller;
    }

    public MatchSettings(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
        this.controller = new Controller(model, wargroove, this);
    }


    @Override
    public void init() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply();
        buttonSound = getAssets().getDefault(Sound.class);

        back = new TextButton("Back", skin);
        reset = new TextButton("Reset", skin);

        weatherLabel = new Label("Weather :", skin);
        turnTimeLabel = new Label("Turn Timer :", skin);
        fogLabel = new Label("Fog of War :", skin);
        incomeLabel = new Label("Income :", skin);
        printIncome = new Label("0",skin);
        biomeLabel = new Label("Biome :", skin);
        commandersLabel = new Label("Commanders :", skin);
        teamLabel = new Label("Teams :", skin);

        weather = new SelectBox<String>(skin);
        weather.setItems("Random", "Good Weather", "Bad Weather", "Stormy");
        turnTime = new SelectBox<String>(skin);
        turnTime.setItems("Off","On");
        fog = new SelectBox(skin);
        fog.setItems("Off", "On");
        income = new Slider(0,1000,50,false,skin);
        income.setValue(0);
        biome = new SelectBox(skin);
        biome.setItems("Ice","Forest","Desert","Jungle");
        commanders = new SelectBox(skin);
        commanders.setItems("Normal");
        team = new SelectBox(skin);
        team.setItems("Default");

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
    private Table drawTable(){
        Table table = new Table(skin);
        table.setFillParent(true);

        table.add(weatherLabel).padLeft(20f);
        table.add(weather);
        table.row();
        table.add(turnTimeLabel).padLeft(20f);
        table.add(turnTime);
        table.row();
        table.add(fogLabel).padLeft(20f);
        table.add(fog);
        table.row();
        table.add(incomeLabel).padLeft(20f);
        table.add(income);
        table.add(printIncome);
        table.row();
        table.add(biomeLabel).padLeft(20f);
        table.add(biome);
        table.row();
        table.add(commandersLabel);
        table.add(commanders);
        table.row();
        table.add(teamLabel);
        table.add(team);

        table.row();
        table.add(back);
        table.add(reset);

        return table;
    }

    /**
     * Init the buttons listener.
     */
    private void initListener() {


        back.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        makeSound(buttonSound);
                        controller.back();
                    }
                }
        );
        income.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        makeSound(buttonSound);
                        printIncome.setText(income.getValue() +"");
                    }
                }
        );
    }
}
