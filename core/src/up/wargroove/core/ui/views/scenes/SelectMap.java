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
 */
public class SelectMap extends ViewWithPrevious {
    Description description;
    /**
     * Previous screen button.
     */
    private Button back;
    private OrthogonalTiledMapRenderer renderer;
    private Pair<Float, Float> mapSize;
    private Stage VRT;
    private Stage VRB;
    private Stage VL;
    private Sound buttonSound;
    private Database database;
    private String collectionName;
    private Button choseMap;
    private Stage stage;

    public SelectMap(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous, controller, model, wargroove);
    }

    private static String transform(String mapName) {
        String s = mapName.toLowerCase(Locale.ROOT);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public void init() {
        setStage(new ScreenViewport());
        stage = getStage();
        renderer = new OrthogonalTiledMapRenderer(new TiledMap());
        mapSize = new Pair<>(0f, 0f);
        VL = new Stage(new ScreenViewport());
        VRT = new Stage(new ScreenViewport());
        VRB = new Stage(new ScreenViewport());
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = getAssets().getDefault(Sound.class);
        back = new TextButton("Back", skin);
        choseMap = new TextButton("Choose this Map", skin);
        boolean b = getModel().getProperties() != null;
        choseMap.setDisabled(!b);
        choseMap.setVisible(b);


        collectionName = "worlds";
        DBEngine.getInstance().connect();
        database = DBEngine.getInstance().getDatabase("wargroove");
        database.selectCollection(collectionName);

        Table VLTable = new Table();
        VLTable.setFillParent(true);
        ScrollPane buttons = new ScrollPane(initButtonsTable(database.getKeys()), skin);
        buttons.setCancelTouchFocus(false);
        buttons.setSmoothScrolling(true);
        VLTable.add(buttons).expand().fill();
        VLTable.row();
        VL.addActor(VLTable);

        new MapActor();

        Table VRBTable = new Table();
        VRBTable.setFillParent(true);
        description = new Description();
        VRBTable.addActor(description);
        VRBTable.add(back);
        VRBTable.add(choseMap);
        VRB.addActor(VRBTable);
        addInput(VRB, VL);

        initListener();

    }

    /**
     * Init the table.
     *
     * @return The table.
     */
    private Table initButtonsTable(List<String> mapNames) {
        Table table = new Table();
        table.setFillParent(true);
        mapNames.forEach(name -> {
            table.add(new MapButton(name));
            table.row();
        });
        return table;
    }

    @Override
    public void resize(int width, int height) {
        int newWidth = width / 2;
        int newHeight = height / 2;
        VL.getViewport().update(newWidth, height, true);
        VRT.getViewport().update(newWidth, newHeight, true);
        VRB.getViewport().update(newWidth, newHeight, true);
        VRT.getViewport().setScreenPosition(newWidth, newHeight);
        VRB.getViewport().setScreenX(newWidth);
        if (getModel().getWorld() != null) {
            VRT.clear();
            buildMap(getModel().getWorld());
        }
    }

    @Override
    public void draw(float delta) {
        int width = Gdx.graphics.getWidth() / 2;
        int height = Gdx.graphics.getHeight() / 2;
        VL.getViewport().apply();
        VL.draw();
        VRT.getViewport().setScreenX((int) (3 * width - mapSize.first) / 2);
        VRT.getViewport().setScreenY((int) (3 * height - mapSize.second) / 2);
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
        //buttonSound.dispose();
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

    private void setDescription(String mapName) {
        DbObject object = database.get(mapName + "/");
        if (object == null) {

            return;
        }
        WorldProperties properties = new WorldProperties();
        properties.load(object);
        getModel().setWorld(properties);
        VRT.clear();
       buildMap(getModel().getWorld());
        description.setInformation(properties);
    }

    private MapActor buildMap(World world) {
        MapActor newMap = new MapActor(world);
        TiledMap last = renderer.getMap();
        renderer.dispose();
        mapSize.first = (float) (newMap.map.getHeight() * newMap.map.getTileSize());
        mapSize.second = (float) (newMap.map.getWidth() * newMap.map.getTileSize());
        float heightRatio = (VRT.getHeight() / mapSize.first);
        float widthRatio = (VRT.getWidth() / mapSize.second);

        renderer = new OrthogonalTiledMapRenderer(newMap.map, Math.min(heightRatio, widthRatio));
        mapSize.first *= renderer.getUnitScale();
        mapSize.second *= renderer.getUnitScale();
        System.out.println(renderer.getUnitScale());
        renderer.setMap(newMap.map);
        System.out.println(newMap.map.getScale());
        last.dispose();
        return newMap;
    }

    @Override
    public void setDebug(boolean debug) {
        super.setDebug(debug);
        VL.setDebugAll(debug);
        VRT.setDebugAll(debug);
        VRB.setDebugAll(debug);
    }

    private class MapButton extends TextButton {

        private final String mapName;

        public MapButton(String mapName) {
            super(
                    transform(mapName),
                    Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "rusty-robot-ui.json", Skin.class)
            );
            this.mapName = mapName;
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

    private class Description extends Table {
        private MapActor map;

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }

        public void setInformation(WorldProperties properties) {
        }
    }

    private class MapActor {
        private final GameMap map;

        private MapActor(World world) {
            float heightRatio = (VRT.getHeight() / (world.getDimension().first * 64));
            float widthRatio = (VRT.getWidth() / (world.getDimension().second * 64));
            float scale = Math.min(heightRatio,widthRatio);
            System.out.println(scale);

            map = new GameMap(world, VRT, getController(), scale,true,false);
        }

        private MapActor() {
            this.map = null;
        }

        public void dispose() {
            if (map != null) {
                map.dispose();
            }
        }
    }

}
