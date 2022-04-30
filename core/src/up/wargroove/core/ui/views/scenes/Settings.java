package up.wargroove.core.ui.views.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import up.wargroove.core.WargrooveClient;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;

/**
 * The World Settings Menu.
 */
public class Settings extends ViewWithPrevious {

    private final Button back;
    private final Slider cameraZoomVelocity;
    private final Slider musicVolume;
    private final CheckBox fullScreen;
    private final Label printCameraVelocity;
    private final Label printCameraZoomVelocity;
    private final Label fullScreenLabel;
    private final Label cameraVelocityLabel;
    private final Label cameraZoomVelocityLabel;
    private final Label musicVolumeLabel;
    private final Label soundVolumeLabel;
    private final Slider soundVolume;
    private final ScrollPane pane;

    private final Slider cameraVelocity;

    Sound buttonSound;

    public Settings(View previous, Controller controller, Model model, WargrooveClient wargroove) {
        super(previous, controller, model, wargroove);
        Skin skin = getAssets().getSkin();
        buttonSound = Assets.getInstance().getDefault(Sound.class);
        fullScreenLabel = new Label("Full screen", skin);
        cameraVelocityLabel = new Label("Camera velocity", skin);
        cameraZoomVelocityLabel = new Label("Zoom velocity", skin);
        musicVolumeLabel = new Label("Music Volume", skin);
        soundVolumeLabel = new Label("Sound Volume", skin);
        back = new TextButton("Back", skin);
        cameraVelocity = new Slider(1, 100, 1, false, skin);
        printCameraVelocity = new Label("", skin);
        printCameraZoomVelocity = new Label("", skin);
        cameraZoomVelocity = new Slider(1, 100, 1, false, skin);
        musicVolume = new Slider(0, 100, 10, false, skin);
        soundVolume = new Slider(0, 100, 10, false, skin);
        fullScreen = new CheckBox("", skin);
        pane = new ScrollPane(null, skin);
        setSettings();
    }

    /**
     * Sets the screen default values according to the values stores in the client' settings.
     */
    private void setSettings() {
        cameraVelocity.setValue(getClient().getCameraVelocity() * 100);
        printCameraVelocity.setText(cameraVelocity.getValue() + " %");
        cameraZoomVelocity.setValue(getClient().getCameraZoomVelocity() * 100);
        printCameraZoomVelocity.setText(cameraZoomVelocity.getValue() + " %");
        musicVolume.setValue(getClient().getMusicVolume() * 100);
        soundVolume.setValue(getClient().getSoundVolume() * 100);
        fullScreen.setChecked(getClient().isFullScreen());
    }

    @Override
    public void init() {
        Viewport viewport = new ScreenViewport();
        viewport.apply();
        initListener();
        setStage(viewport);
        pane.setActor(drawTable());
        pane.setColor(Color.BLACK);
        pane.setScrollingDisabled(true, false);
        pane.setScrollbarsVisible(true);
        pane.setCancelTouchFocus(false);
        pane.setSmoothScrolling(true);
        Table table = new Table();
        table.setFillParent(true);
        table.add(pane).expand().fill();
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
                "", Assets.getInstance().getSkin()
        );

        table.setFillParent(true);
        table.add(fullScreenLabel).padRight(padR).left();
        table.add(fullScreen).center().pad(padC);
        table.row();
        table.add(musicVolumeLabel).padRight(padR).left();
        table.add(musicVolume).center().pad(padC);
        table.row();
        table.add(soundVolumeLabel).padRight(padR).left();
        table.add(soundVolume).center().pad(padC);
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
                        getController().back(getPrevious());
                        pane.cancel();
                    }
                }
        );

        musicVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                getClient().setMusicVolume(musicVolume.getValue() / 100);
                pane.cancel();
            }
        });
        soundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                getClient().setSoundVolume(soundVolume.getValue() / 100);
                pane.cancel();
            }
        });
        cameraVelocity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                printCameraVelocity.setText(cameraVelocity.getValue() + "%");
                getClient().setCameraVelocity(cameraVelocity.getValue() / 100);
                pane.cancel();
            }
        });
        cameraZoomVelocity.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                printCameraZoomVelocity.setText(cameraZoomVelocity.getValue() + "%");
                getClient().setCameraZoomVelocity(cameraZoomVelocity.getValue() / 100);
                pane.cancel();
            }
        });

        fullScreen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getController().playSound(buttonSound);
                getClient().setFullScreen(fullScreen.isChecked());
                pane.cancel();
            }
        });
    }
}
