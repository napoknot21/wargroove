package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.WorldProperties;

import java.nio.channels.AcceptPendingException;

/**
 * The World Settings Menu.
 */
public class MatchSettings extends ViewWithPrevious {
    /**
     * Previous screen button.
     */
    private Button back;
    /**
     * Button to reset the settings to the defaults values
     */
    private Button reset;

    /**
     * Button to send the configuration into the model
     */
    private Button chooseConfig;
    /**
     * the viewport
     */
    private Viewport viewport;

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
    //private Label turnTimeLabel;
    /**
     * label of the fog SelectBox
     */
    private Label fogLabel;
    private Label incomeLabel;
    private Label printIncome;
    private Label biomeLabel;
    //private Label commandersLabel;
    //private Label teamLabel;
    /**
     * Weather SelectBox
     */
    private SelectBox weather;
    //private SelectBox turnTime;
    private SelectBox fog;
    private Slider income;
    private SelectBox biome;
    //private SelectBox commanders;
    //private SelectBox team;

    private CheckBox checkFog;
    private Label checkText;

    private Skin skin;

    WorldProperties properties = new WorldProperties();




    private int HEIGHT = Gdx.graphics.getHeight();
    private int WIDTH = Gdx.graphics.getWidth();

    public MatchSettings(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous,controller, model, wargroove);
        this.skin = getAssets().getDefault(Skin.class);

    }


    @Override
    public void init() {
        viewport = new ScreenViewport();
        viewport.apply();
        buttonSound = getAssets().getDefault(Sound.class);

        back = new TextButton("Back", skin);
        reset = new TextButton("Reset", skin);
        chooseConfig = new TextButton("Choose this configuration", skin);

        weatherLabel = new Label("Weather :", skin);
        //turnTimeLabel = new Label("Turn Timer :", skin);
        fogLabel = new Label("Fog of War :", skin);
        checkText = new Label("On", skin);
        incomeLabel = new Label("Income :", skin);
        printIncome = new Label("500.0",skin);
        biomeLabel = new Label("Biome :", skin);
        //commandersLabel = new Label("Commanders :", skin);
        //teamLabel = new Label("Teams :", skin);

        weather = new SelectBox<String>(skin);
        weather.setItems("Random", "Good Weather", "Bad Weather", "Stormy");
        //turnTime = new SelectBox<String>(skin);
        //turnTime.setItems("Off","On");
        //fog = new SelectBox(skin);
        //fog.setItems("Off", "On");
        checkFog = new CheckBox("On" , skin);
        checkFog.setChecked(true);
        income = new Slider(20,500,10,false,skin);
        income.setValue(100);
        biome = new SelectBox(skin);
        biome.setItems(Biome.GRASS,Biome.ICE,Biome.DESERT,Biome.VOLCANO);
        //commanders = new SelectBox(skin);
        //commanders.setItems("Normal");
        //team = new SelectBox(skin);
        //team.setItems("Default");

        initListener();
        setStage(viewport);
        addActor(drawTable());
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void draw(float delta) {
        getStage().act(delta);
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
        table.add(weather).padBottom(20f);
        table.row();
        /*table.add(turnTimeLabel).padLeft(20f);
        table.add(turnTime);
        table.row();*/
        table.add(fogLabel).padLeft(20f);
        table.add(checkFog).padBottom(20f);
        table.add(checkText);
        table.row();
        table.add(incomeLabel).padLeft(20f);
        table.add(income).padBottom(20f);
        table.add(printIncome);
        table.row();
        table.add(biomeLabel).padLeft(20f);
        table.add(biome);
        table.row();
        /*table.add(commandersLabel);
        table.add(commanders);
        table.row();
        table.add(teamLabel);
        table.add(team);

        table.row();*/
        table.add(back);
        table.add(reset);
        table.add(chooseConfig);

        return table;
    }

    /**
     * Init the buttons listener.
     */
    private void initListener() {


        back.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().back(getPrevious());
                    }
                }
        );
        income.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        printIncome.setText(income.getValue() +"%");
                        properties.setIncome((int)income.getValue());
                    }
                }
        );
        reset.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        weather.setSelected("Random");
                        biome.setSelected("Grass");
                        checkFog.setChecked(true);
                        properties.setFog(true);
                        checkText.setText("On");
                        income.setValue(500);
                        printIncome.setText("500.0");
                        properties.setIncome(500);
                    }
                }
        );
        /*biome.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        makeSound(buttonSound);
                        properties.setBiome((Biome) biome.getSelected());
                    }
                }
        );*/
        checkFog.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        if(checkFog.isChecked()){
                            checkText.setText("On");
                            properties.setFog(true);
                        }
                        else{
                            checkText.setText("Off");
                            properties.setFog(false);
                        }
                    }
                }
        );

        chooseConfig.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        properties.setBiome((Biome) biome.getSelected());
                        getModel().setProperties(properties);
                        getController().startGame();
                    }
                }
        );
    }
}
