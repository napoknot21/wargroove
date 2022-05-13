package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.Controller;
import up.wargroove.core.ui.views.objects.MapActor;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Pair;

/**
 * The World Settings Menu.
 */
public class MatchSettings extends ViewWithPrevious {
    private final WorldProperties properties;
    private final Skin skin;
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
     * The sound of the buttons
     */
    private Sound buttonSound;
    private Label incomeLabel;
    private Label printIncome;
    private Label biomeLabel;
    private Biome last;
    /**
     * Weather SelectBox
     */
    private SelectBox<String> weather;
    private Slider income;
    private SelectBox<Biome> biome;
    private Stage VLT;
    private Stage VRT;
    private Stage VB;
    private OrthogonalTiledMapRenderer renderer;
    private Pair<Float, Float> mapSize;

    /**
     * COnstructor for MatchSetting
     * @param previous previous view
     * @param controller the controller
     * @param model the model
     * @param wargroove the wargrooveClient
     */
    public MatchSettings(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous, controller, model, wargroove);
        this.skin = getAssets().getSkin();
        properties = (getModel().getProperties() != null) ? getModel().getProperties() : new WorldProperties();
    }


    @Override
    public void init() {
        setStage(new ScreenViewport());
        VLT = new Stage(new ScreenViewport());
        VRT = new Stage(new ScreenViewport());
        VB = new Stage(new ScreenViewport());
        buttonSound = getAssets().getSound();
        back = new TextButton("Back", skin);
        reset = new TextButton("Reset", skin);
        chooseConfig = new TextButton("Choose this configuration", skin);
        incomeLabel = new Label("Income :", skin);
        income = new Slider(20, 500, 10, false, skin);
        income.setValue(100);
        printIncome = new Label(income.getValue() + "%", skin);
        biomeLabel = new Label("Biome :", skin);
        weather = new SelectBox<>(skin);
        weather.setAlignment(Align.center);
        weather.setItems("Random", "Good Weather", "Bad Weather", "Stormy");
        biome = new SelectBox<>(skin);
        biome.setAlignment(Align.center);
        biome.setItems(Biome.GRASS, Biome.ICE, Biome.DESERT, Biome.VOLCANO);
        last = properties.getBiome();
        initListener();
        VLT.addActor(buildLeftTop());
        VB.addActor(buildBottom());
        addInput(VRT, VLT, VB);
        mapSize = new Pair<>(0f, 0f);
        renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
    }

    /**
     * Build a screen table on the left - top
     * @return the screen table
     */
    private Table buildLeftTop() {
        Table intel = new Table(skin);
        intel.setFillParent(true);
        intel.add(biomeLabel).padLeft(20f).expand();
        intel.add(biome).expand();
        intel.row();
        intel.add(incomeLabel).padLeft(20f).expand();
        intel.add(income).padBottom(20f).expand();
        printIncome.setText("9999%");
        intel.add(printIncome).size(printIncome.getWidth());
        printIncome.setText(income.getValue() + "%");
        intel.row();
        return intel;
    }

    @Override
    public void draw(float delta) {
        if (last != biome.getSelected()) {
            VRT.clear();
            renderer.getMap().dispose();
            renderer.dispose();
            getModel().getProperties().setBiome(biome.getSelected());
            renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
        }
        last = biome.getSelected();
        int width = Gdx.graphics.getWidth() / 2;
        int height = Gdx.graphics.getHeight() / 2;
        VLT.getViewport().setScreenHeight(height);
        VLT.getViewport().apply();
        VLT.act(delta);
        VLT.draw();
        VRT.getViewport().setScreenX((int) (3 * width - mapSize.first) / 2);
        VRT.getViewport().setScreenY((int) (3 * height - mapSize.second) / 2);
        VRT.getViewport().apply();
        renderer.setView((OrthographicCamera) VRT.getCamera());
        renderer.render();
        VRT.draw();
        VB.getViewport().apply();
        VB.draw();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        int newWidth = width / 2;
        int newHeight = height / 2;
        VLT.getViewport().update(newWidth, newHeight, true);
        VRT.getViewport().update(newWidth, newHeight, true);
        VB.getViewport().update(width, newHeight, true);
        VRT.getViewport().setScreenPosition(newWidth, newHeight);
        VLT.getViewport().setScreenY(newHeight);

        if (getModel().getWorld() != null) {
            VRT.clear();
            renderer.getMap().dispose();
            renderer.dispose();
            renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        VRT.dispose();
        VLT.dispose();
        VB.dispose();
        renderer.getMap().dispose();
        renderer.dispose();
    }


    /**
     * Init the table.
     *
     * @return The table.
     */
    private Table buildBottom() {

        Table buttons = new Table();
        buttons.center();
        buttons.setFillParent(true);
        buttons.add(back).pad(10);
        buttons.add(reset).pad(10);
        buttons.add(chooseConfig).pad(10);
        return buttons;
    }

    /**
     * Init the buttons' listener.
     */
    private void initListener() {

        back.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getModel().setWorld(null);
                        getController().back(getPrevious());
                    }
                }
        );
        income.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        printIncome.setText(income.getValue() + "%");
                    }
                }
        );
        reset.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        weather.setSelected("Random");
                        biome.setSelected(Biome.GRASS);
                        getModel().getProperties().setBiome(biome.getSelected());
                        last = biome.getSelected();
                        VRT.clear();
                        renderer.getMap().dispose();
                        renderer.dispose();
                        renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
                        income.setValue(100);
                        printIncome.setText(income.getValue() + "%");

                    }
                }
        );

        chooseConfig.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        properties.setBiome(biome.getSelected());
                        properties.setIncome(income.getValue() / 100f);
                        properties.setIncome(income.getValue() / 100f);
                        getModel().setProperties(properties);
                        getController().startGame();
                    }
                }
        );
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        VB.setDebugAll(debug);
        VRT.setDebugAll(debug);
        VLT.setDebugAll(debug);
    }
}
