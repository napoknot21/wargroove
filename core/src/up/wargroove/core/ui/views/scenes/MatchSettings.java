package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.objects.MapActor;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Pair;

/**
 * The World Settings Menu.
 */
public class MatchSettings extends ViewWithPrevious {
    private final WorldProperties properties;
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
     * label of the timer SelectBox
     */
    //private Label turnTimeLabel;
    /**
     * label of the weather SelectBox
     */
    private Label weatherLabel;
    /**
     * label of the fog SelectBox
     */
    private Label fogLabel;
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
    private CheckBox checkFog;
    private final Skin skin;
    private Stage VLT;
    private Stage VRT;
    private Stage VB;
    private OrthogonalTiledMapRenderer renderer;
    private Pair<Float, Float> mapSize;

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
        buttonSound = getAssets().getDefault(Sound.class);
        back = new TextButton("Back", skin);
        reset = new TextButton("Reset", skin);
        chooseConfig = new TextButton("Choose this configuration", skin);
        weatherLabel = new Label("Weather :", skin);
        fogLabel = new Label("Fog of War :", skin);
        incomeLabel = new Label("Income :", skin);
        income = new Slider(20, 500, 10, false, skin);
        income.setValue(100);
        printIncome = new Label(income.getValue() + "%", skin);
        biomeLabel = new Label("Biome :", skin);
        weather = new SelectBox<String>(skin);
        weather.setAlignment(Align.center);
        weather.setItems("Random", "Good Weather", "Bad Weather", "Stormy");
        checkFog = new CheckBox("On", skin);
        checkFog.setChecked(true);
        biome = new SelectBox<Biome>(skin);
        biome.setAlignment(Align.center);
        biome.setItems(Biome.GRASS, Biome.ICE, Biome.DESERT, Biome.VOLCANO);
        last = Biome.GRASS;
        initListener();
        VLT.addActor(buildLeftTop());
        VB.addActor(buildBottom());
        addInput(VRT, VLT, VB);
        mapSize = new Pair<>(0f,0f);
        renderer = MapActor.buildMap(getModel().getWorld(),VRT,mapSize,getController());
    }

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
        printIncome.setText(income.getValue() +"%");
        intel.row();
        return intel;
    }

    @Override
    public void draw(float delta) {
        if (last != biome.getSelected()) {
            VRT.clear();
            renderer.getMap().dispose();
            renderer.dispose();
            renderer = MapActor.buildMap(getModel().getWorld(),VRT,mapSize,getController());
            last = biome.getSelected();
        }
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
            renderer = MapActor.buildMap(getModel().getWorld(),VRT,mapSize,getController());
        }
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

        biome.getClickListener();

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
                        renderer = MapActor.buildMap(getModel().getWorld(),VRT,mapSize,getController());
                        checkFog.setChecked(true);
                        income.setValue(100);
                        printIncome.setText(income.getValue() + "%");

                    }
                }
        );
        checkFog.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
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
                        properties.setFog(checkFog.isChecked());
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
