package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.objects.GameMap;
import up.wargroove.core.ui.views.objects.MapActor;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.DBEngine;
import up.wargroove.utils.Database;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Pair;

import java.util.List;
import java.util.Locale;

/**
 * The World Settings Menu.
 * @see up.wargroove.core.ui.views.scenes.ViewWithPrevious
 * @see up.wargroove.core.ui.views.scenes.View
 */
public class SelectMap extends ViewWithPrevious {
    /**
     * Previous screen button.
     */
    private Button back;
    private OrthogonalTiledMapRenderer renderer;
    private Pair<Float, Float> mapSize;
    /**
     * The right top stage.
     */
    private Stage VRT;
    /**
     * The right bottom stage.
     */
    private Stage VRB;
    /**
     * The left stage
     */
    private Stage VL;
    /**
     * The right stage.
     */
    private Stage VT;
    private Sound buttonSound;
    private Database database;
    private String collectionName;
    private Button choseMap;
    private ScrollPane buttons;
    /**
     * the amount of players.
     */
    private int amt = 2;

    public SelectMap(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous, controller, model, wargroove);
    }

    /**
     * Transform the string into a pretty one.
     * @param mapName The string which will be transformed.
     * @return The pretty sting.
     */
    private static String transform(String mapName) {
        String s = mapName.toLowerCase(Locale.ROOT);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public void init() {
        setStage(new ScreenViewport());
        renderer = new OrthogonalTiledMapRenderer(new TiledMap());
        mapSize = new Pair<>(0f, 0f);
        VL = new Stage(new ScreenViewport());
        VRT = new Stage(new ScreenViewport());
        VRB = new Stage(new ScreenViewport());
        VT = new Stage(new ScreenViewport());
        Skin skin = getAssets().getSkin();
        buttonSound = getAssets().getDefault(Sound.class);
        back = new TextButton("Back", skin);
        choseMap = new TextButton("Select", skin);
        boolean b = getModel().getProperties() != null;
        choseMap.setDisabled(!b);
        choseMap.setVisible(b);
        collectionName = "";
        DBEngine.getInstance().connect();
        database = DBEngine.getInstance().getDatabase("wargroove");
        database.selectCollection(collectionName);
        VT.addActor(buildCategories());
        Table VLTable = new Table();
        VLTable.setFillParent(true);
        buttons = new ScrollPane(initButtonsTable(database.getKeys()), skin);
        VLTable.add(buttons).expand().fill();
        VLTable.row();
        VL.addActor(VLTable);
        Table VRBTable = new Table();
        VRBTable.setFillParent(true);
        VRBTable.bottom();
        VRBTable.add(back).pad(10);
        VRBTable.add(choseMap).pad(10);
        VRB.addActor(VRBTable);
        addInput(VRB, VT, VL);
        initListener();

    }

    /**
     * Builds the maps categories.
     * @return the generated table with the categories inside.
     */
    private Table buildCategories() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        for (int i = 2; i < 5; i++) {
            table.add(new CategoryButton(i)).expandX().fill().pad(10);
        }
        return table;
    }

    /**
     * Init the table.
     *
     * @return The table.
     */
    public Table initButtonsTable(List<String> mapNames) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        if (mapNames != null) {
            mapNames.forEach(name -> {
                table.add(new MapButton(name)).expandX().pad(10);
                table.row();
            });
        }
        return table;
    }

    @Override
    public void resize(int width, int height) {
        int newWidth = width / 2;
        int newHeight = height / 2;
        VT.getViewport().update(width, newHeight / 2, true);
        VL.getViewport().update(newWidth, (3 * height) / 4, true);
        VRT.getViewport().update(newWidth, newHeight, true);
        VRB.getViewport().update(newWidth, newHeight / 2, true);
        VRT.getViewport().setScreenPosition(newWidth, newHeight / 2);
        VRB.getViewport().setScreenX(newWidth);
        if (getModel().getWorld() != null) {
            VRT.clear();
            renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
        }
    }

    @Override
    public void draw(float delta) {
        int width = Gdx.graphics.getWidth() / 2;
        int height = Gdx.graphics.getHeight() / 2;
        VL.getViewport().apply();
        VL.draw();
        VT.getViewport().setScreenY((3 * height) / 2);
        VT.getViewport().apply();
        VT.draw();
        VRT.getViewport().setScreenX((int) (3 * width - mapSize.first) / 2);
        VRT.getViewport().setScreenY((int) (3 * height - mapSize.second) / 4);
        VRT.getViewport().apply();
        renderer.setView((OrthographicCamera) VRT.getCamera());
        renderer.render();
        VRT.draw();
        VRB.getViewport().setScreenX(width);
        VRB.getViewport().apply();
        VRB.draw();
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
        VRT.dispose();
        VL.dispose();
        VT.dispose();
        VRB.dispose();
        renderer.getMap().dispose();
        renderer.dispose();
    }

    /**
     * Init the buttons' listener.
     */
    private void initListener() {
        choseMap.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().openMatchSettings();
                        DBEngine.getInstance().disconnect();
                    }
                }
        );
        back.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getModel().setWorld(null);
                        getController().back(getPrevious());
                        DBEngine.getInstance().disconnect();
                    }
                }
        );
    }

    /**
     * Sets the map description.
     *
     * @param mapName The map name.
     */
    private void setDescription(String mapName) {
        DbObject object = database.get(mapName + "/");
        if (object == null) {

            return;
        }
        WorldProperties properties = new WorldProperties();
        properties.load(object);
        properties.amt = amt;
        getModel().setWorld(properties);
        VRT.clear();
        renderer = MapActor.buildMap(getModel().getWorld(), VRT, mapSize, getController());
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        VL.setDebugAll(debug);
        VRT.setDebugAll(debug);
        VRB.setDebugAll(debug);
        VT.setDebugAll(debug);
    }

    /**
     * Button which represent a map
     * @see TextButton
     */
    private class MapButton extends TextButton {
        public MapButton(String mapName) {
            super(
                    transform(mapName),
                    Assets.getInstance().getSkin()
            );
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getController().playSound(buttonSound);
                    setDescription(mapName);
                    choseMap.setDisabled(false);
                    choseMap.setVisible(true);
                }
            });
        }
    }

    /**
     * Button which represent a category.
     * @see TextButton
     */
    private class CategoryButton extends TextButton {
        public CategoryButton(int i) {
            super(
                    Integer.toString(i),
                    Assets.getInstance().getSkin()
            );
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getController().playSound(buttonSound);
                    amt = i;
                    collectionName = i + "_players";
                    getController().changeCategory(collectionName, database, buttons);
                }
            });
        }
    }
}
