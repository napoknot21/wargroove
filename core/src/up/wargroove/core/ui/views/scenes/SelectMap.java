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
    private Stage VT;
    private Sound buttonSound;
    private Database database;
    private String collectionName;
    private Button choseMap;
    private ScrollPane buttons;
    private int amt = 2;

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
        //buttons.setCancelTouchFocus(false);
        //buttons.setSmoothScrolling(true);

        //categories.setCancelTouchFocus(false);
        //categories.setSmoothScrolling(true);
        VLTable.add(buttons).expand().fill();
        VLTable.row();
        VL.addActor(VLTable);

        new MapActor();

        Table VRBTable = new Table();
        VRBTable.setFillParent(true);
        VRBTable.bottom();
        description = new Description();
        VRBTable.addActor(description);
        VRBTable.add(back);
        VRBTable.add(choseMap);
        VRB.addActor(VRBTable);
        addInput(VRB, VT,VL);

        initListener();

    }

    private Table buildCategories() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        for (int i = 2; i < 5; i++) {
            table.add(new CategoryButton(i)).expandX().fill();
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
        if (mapNames != null) {
            mapNames.forEach(name -> {
                table.add(new MapButton(name));
                table.row();
            });
        }
        return table;
    }

    @Override
    public void resize(int width, int height) {
        int newWidth = width / 2;
        int newHeight = height / 2;
        VT.getViewport().update(width,newHeight/2, true);
        VL.getViewport().update(newWidth, (3 * height)/4, true);
        VRT.getViewport().update(newWidth, newHeight, true);
        VRB.getViewport().update(newWidth, newHeight / 2, true);
        VRT.getViewport().setScreenPosition(newWidth, newHeight/2);
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
        VT.getViewport().setScreenY((3*height)/2);
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
        properties.amt = amt;
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
        VT.setDebugAll(debug);
    }

    private class MapButton extends TextButton {

        private final String mapName;

        public MapButton(String mapName) {
            super(
                    transform(mapName),
                    Assets.getInstance().getSkin()
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
                    getController().changeCategory(collectionName,database,buttons);
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
