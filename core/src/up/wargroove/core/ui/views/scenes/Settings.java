package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
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

/**
 * The World Settings Menu.
 */
public class Settings extends View {
    /**
     * Full Screen button.
     */
    private Button back;
    /**
     * Sound title label.
     */
    private Label soundLabel;
    /**
     * Sound state label.
     */
    private Label stateLabel;
    /**
     * To put the sound on
     */
    private CheckBox sound;

    private Viewport viewport;

    Sound buttonSound;

    public Settings(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = getAssets().getDefault(Sound.class);
        soundLabel = new Label("Sound", skin);
        sound = new CheckBox("On",skin);
        sound.setChecked(controller.isSoundOn());
        stateLabel = new Label("", skin);
        back = new TextButton("Back", skin);

    }

    /*public Settings(Controller controller, WargrooveClient wargroove) {
        this(controller, null, wargroove);
        this.controller = controller;
    }

    public Settings(Model model, WargrooveClient wargroove) {
        super(model, wargroove);
        this.controller = new Controller(model, wargroove, this);
    }*/

    @Override
    public void init() {
        viewport = new ScreenViewport();
        viewport.apply();
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
    private Table drawTable() {
        Table table = new Table();
        table.setFillParent(true);
        //table.top();
        /*table.add(dimension);
        table.row();*/
        table.add(soundLabel).padRight(20f);
        table.add(sound);
        table.add(stateLabel);
        table.row();
        table.add(back);


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
                        getController().back();
                    }
                }
        );
        sound.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(sound.isChecked()){
                            stateLabel.setText("On");
                            getController().setSound(true);
                            getController().playSound(buttonSound);
                        }
                        else{
                            stateLabel.setText("Off");
                            getController().setSound(false);
                        }
                    }
                }
        );
    }
}
