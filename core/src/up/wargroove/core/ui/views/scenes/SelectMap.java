package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

/**
 * The World Settings Menu.
 */
public class SelectMap extends ViewWithPrevious {
    /**
     * Dimension button.
     */
    //private Label dimension;
    /**
     * Previous screen button.
     */
    private Button back;
    /**
     * Slider
     */
    //private Slider dimSlider;
    /**
     * Screen controller.
     */
    private Controller controller;

    /**
     * Button map 1
     */
    private Button map1;
    /**
     * Button map 2
     */
    private Button map2;
    /**
     * Button map 3
     */
    private Button map3;

    private Viewport viewport;

    private Sound buttonSound;

    private SpriteBatch sb;

    private Texture mapText1;

    public SelectMap(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous, controller, model, wargroove);
    }


    @Override
    public void init() {
        viewport = new ScreenViewport();
        viewport.apply();
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = getAssets().getDefault(Sound.class);
        //dimSlider = new Slider(0.5f, 20f, 0.1f, false,skin);
        //dimension = new Label("Dimension", skin);
        map1 = new TextButton("Choose Map 1",skin);
        map2 = new TextButton("Choose Map 2",skin);
        map3 = new TextButton("Choose Map 3",skin);
        back = new TextButton("Back", skin);

        sb = new SpriteBatch();
        mapText1 = new Texture(Gdx.files.internal("data/sprites/map1.PNG"));
        initListener();
        setStage(viewport);
        addActor(drawTable());
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void draw(float delta) {
        getStage().draw();
    }

    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        draw(delta);
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
        sb.dispose();
        mapText1.dispose();
        //buttonSound.dispose();
    }

    /**
     * Init the table.
     *
     * @return The table.
     */
    private Table drawTable() {
        Table table = new Table();
        table.setFillParent(true);
        //table.top();
        //table.add(dimension);
        table.add(map1).padRight(60f);
        table.add(map2).padRight(60f);
        table.add(map3);
        table.row();
        //table.add(dimSlider);
        table.row();
        table.add(back);

        return table;
    }

    /**
     * Init the buttons listener.
     */
    private void initListener() {
        map1.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().openMatchSettings();
                    }
                }
        );

        map2.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().openMatchSettings();
                    }
                }
        );
        map3.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().openMatchSettings();
                    }
                }
        );

        back.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        getController().playSound(buttonSound);
                        getController().back(getPrevious());
                    }
                }
        );
    }
}
