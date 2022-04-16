package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

/**
 * The World Settings Menu.
 */
public class Settings extends View {
    /**
     * Full Screen button.
     */
    private final Button back;

    /**
     * Sound state label.
     */
    private final Label stateLabel;

    private final Slider cameraZoomVelocity;
    private final Slider volume;
    private final CheckBox fullScreen;
    private final Label printCameraVelocity;
    private final Label printCameraZoomVelocity;
    private final Label printVolume;
    private final Label fullScreenLabel;
    private final Label cameraVelocityLabel;
    private final Label cameraZoomVelocityLabel;
    private final Label volumeLabel;

    private Slider cameraVelocity;

    private Viewport viewport;

    Sound buttonSound;

    public Settings(Controller controller, Model model, WargrooveClient wargroove) {
        super(controller, model, wargroove);
        Skin skin = getAssets().getDefault(Skin.class);
        buttonSound = Assets.getInstance().getDefault(Sound.class);
        fullScreenLabel = new Label("Full screen",skin);
        cameraVelocityLabel = new Label("Camera velocity", skin);
        cameraZoomVelocityLabel = new Label("Zoom velocity", skin);
        volumeLabel = new Label("Volume",skin);
        stateLabel = new Label("", skin);
        back = new TextButton("Back", skin);
        cameraVelocity = new Slider(1,100,1,false,skin);
        printCameraVelocity = new Label("",skin);
        printCameraZoomVelocity = new Label("",skin);
        cameraZoomVelocity = new Slider(1,100,1,false,skin);
        printVolume = new Label("",skin);
        volume = new Slider(0,100,10,false,skin);
        fullScreen = new CheckBox("", skin);
        setSettings();
    }

    private void setSettings() {
        cameraVelocity.setValue(getClient().getCameraVelocity() * 100);
        printCameraVelocity.setText(cameraVelocity.getValue() + " %");
        cameraZoomVelocity.setValue(getClient().getCameraZoomVelocity() * 100);
        printCameraZoomVelocity.setText(cameraZoomVelocity.getValue() + " %");
        volume.setValue(getClient().getVolume() * 100);
        printVolume.setText(volume.getValue() + " %");
        fullScreen.setChecked(getClient().isFullScreen());
    }

    @Override
    public void init() {
        viewport = new ScreenViewport();
        viewport.apply();
        initListener();
        setStage(viewport);
        ScrollPane pane = new ScrollPane(
                drawTable(),
                Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class)
        );
        pane.setColor(Color.BLACK);
        Table table = new Table();
        table.setFillParent(true);
        table.add(pane);
        addActor(table);
        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void draw(float delta) {
        getStage().act();
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
        float padR = 30f;
        float padL = 10f;
        float padC = 10;
        Label empty = new Label(
                "", Assets.getInstance().get(Assets.AssetDir.SKIN.getPath() + "uiskin.json", Skin.class)
        );

        table.setFillParent(true);
        table.add(fullScreenLabel).padRight(padR).left();
        table.add(fullScreen).center().pad(padC);
        table.row();
        table.add(volumeLabel).padRight(padR).left();
        table.add(volume).center().pad(padC);
        table.add(printVolume).padLeft(padL).right();
        table.row();
        table.add(cameraVelocityLabel).left().padRight(padR);
        table.add(cameraVelocity).center().pad(padC);
        table.add(printCameraVelocity).padLeft(padL).right();
        table.row();
        table.add(cameraZoomVelocityLabel).padRight(padR).left();
        table.add(cameraZoomVelocity).center().pad(padC);
        table.add(printCameraZoomVelocity).padLeft(padL).right();
        table.row();
        table.add(back);
        table.row();
        table.add(empty);
        table.row();
        table.add(empty);

        return table;
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
                        getController().back();
                    }
                }
        );

        volume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                printVolume.setText(volume.getValue() +"%");
                getClient().setVolume(volume.getValue() / 100);
            }
        });
        cameraVelocity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                printCameraVelocity.setText(cameraVelocity.getValue() +"%");
                getClient().setCameraVelocity(cameraVelocity.getValue() / 100);
            }
        });
        cameraZoomVelocity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                printCameraZoomVelocity.setText(cameraZoomVelocity.getValue() +"%");
                getClient().setCameraZoomVelocity(cameraZoomVelocity.getValue() / 100);
            }
        });

        fullScreen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                getClient().setFullScreen(fullScreen.isChecked());
            }
        });
    }
}
