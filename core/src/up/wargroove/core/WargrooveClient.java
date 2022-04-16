package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.scenes.MainMenu;
import up.wargroove.core.ui.views.scenes.View;

/**
 * The wargroove client.
 */
public class WargrooveClient extends Game {

    /**
     * The drawing tool.
     */
    private SpriteBatch batch;

    private Music music;
    /**
     * The app assets.
     */
    private Assets assets;
    /**
     * The client controller.
     */
    private Controller controller;
    /**
     * The shown scene.
     */
    private View scene;
    /**
     * Indicate if the client is in debug mode.
     */
    private final boolean debug;
    private Preferences settings;

    public WargrooveClient(boolean debug) {
        this.debug = debug;
    }

    public WargrooveClient() {
        this(false);
    }

    @Override
    public void create() {
        settings = loadSettings();
        setFullScreen(settings.getBoolean("fullScreen"));
        batch = new SpriteBatch();
        assets = Assets.getInstance();
        assets.loadDefault();
        assets.loadEntitiesDescription();
        Model model = new Model();
        controller = new Controller(model, this);
        controller.create();
        scene = new MainMenu(controller, controller.getModel(), this);
        controller.setScreen(scene);
    }

    private Preferences loadSettings() {
        Preferences preferences = Gdx.app.getPreferences("settings");
        if (!preferences.contains(Settings.VOLUME.name())) {
            preferences.putFloat(Settings.VOLUME.name(), 1f);
            preferences.putFloat(Settings.CAMERA_VELOCITY.name(), 0.50f);
            preferences.putFloat(Settings.CAMERA_ZOOM_VELOCITY.name(), 0.50f);
            preferences.putBoolean(Settings.FULLSCREEN.name(), false);
        }
        return preferences;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        if (scene != null) scene.dispose();
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
        settings.flush();
    }

    @Override
    public void render() {
        super.render();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Assets getAssets() {
        return assets;
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        this.scene = (View) screen;
        if (screen != null) {
            this.scene.setDebug(debug);
        }
    }

    public int getVirtualWidth() {
        return 1920;
    }

    public int getVirtualHeight() {
        return 1080;
    }

    public void setMusic(Music music, boolean looping) {
        this.music = music;
        this.music.setLooping(looping);
    }

    public void stopMusic() {
        stopMusic(false);
    }

    public void stopMusic(boolean delete) {
        if (music == null) return;
        this.music.stop();
        if (delete) {
            music.dispose();
            music = null;
        }
    }

    public void playMusic() {
        if (music != null) {
            music.setVolume(settings.getFloat(Settings.VOLUME.name()));
            music.play();
        }
    }

    public float getCameraVelocity() {
        return settings.getFloat(Settings.CAMERA_VELOCITY.name());
    }

    public void setCameraVelocity(float cameraVelocity) {
        this.settings.putFloat(Settings.CAMERA_VELOCITY.name(), cameraVelocity);
    }

    public float getCameraZoomVelocity() {
        return settings.getFloat(Settings.CAMERA_ZOOM_VELOCITY.name());
    }

    public void setCameraZoomVelocity(float cameraZoomVelocity) {
        settings.putFloat(Settings.CAMERA_ZOOM_VELOCITY.name(), cameraZoomVelocity);
    }

    public float getVolume() {
        return settings.getFloat(Settings.VOLUME.name());
    }

    public void setVolume(float volume) {
        settings.putFloat(Settings.VOLUME.name(), volume);
        if (music != null) {
            music.setVolume(volume);
        }
    }

    public boolean isFullScreen() {
        return settings.getBoolean(Settings.FULLSCREEN.name());
    }

    public void setFullScreen(boolean fullScreen) {
        settings.putBoolean(Settings.FULLSCREEN.name(), fullScreen);
        if (fullScreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(640, 480);
        }
    }

    private enum Settings {
        FULLSCREEN, CAMERA_VELOCITY, CAMERA_ZOOM_VELOCITY, VOLUME
    }
}

