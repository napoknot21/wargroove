package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.scenes.MainMenu;
import up.wargroove.core.ui.views.scenes.View;
import up.wargroove.utils.DBEngine;
import up.wargroove.utils.Database;

/**
 * The wargroove client.
 */
public class WargrooveClient extends Game {

    /**
     * Indicate if the client is in debug mode.
     */
    private final boolean debug;
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
    private Preferences settings;

    public WargrooveClient(boolean debug) {
        this.debug = debug;
    }

    public WargrooveClient() {
        this(false);
    }

    @Override
    public void create() {
        assets = Assets.getInstance();
        assets.load(Assets.AssetDir.SOUND);
        assets.loadDefault();
        assets.load(Assets.AssetDir.SKIN);
        assets.loadEntitiesDescription();
        settings = Gdx.app.getPreferences("settings");
        loadSettings();
        batch = new SpriteBatch();
        Model model = new Model();
        controller = new Controller(model, this);
        controller.create();
        scene = new MainMenu(controller, controller.getModel(), this);
        controller.setScreen(scene);
    }

    private void loadSettings() {
        if (!settings.contains(Settings.MUSIC_VOLUME.name())) {
            setMusicVolume(1f);
        }
        if (!settings.contains(Settings.SOUND_VOLUME.name())) {
            setSoundVolume(1f);
        }
        if (!settings.contains(Settings.CAMERA_VELOCITY.name())) {
            setCameraVelocity(0.50f);
        }
        if (!settings.contains(Settings.CAMERA_ZOOM_VELOCITY.name())) {
            setCameraZoomVelocity(0.50f);
        }
        setFullScreen(
                settings.contains(Settings.FULLSCREEN.name()) && settings.getBoolean(Settings.FULLSCREEN.name())
        );
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
        if (getScreen() != null) {
            getScreen().dispose();
        }
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
            music.setVolume(getMusicVolume());
            music.play();
        }
    }

    public float getCameraVelocity() {
        return settings.getFloat(Settings.CAMERA_VELOCITY.name());
    }

    public void setCameraVelocity(float cameraVelocity) {
        cameraVelocity = Math.max(0, Math.min(1, cameraVelocity));
        this.settings.putFloat(Settings.CAMERA_VELOCITY.name(), cameraVelocity);
    }

    public float getCameraZoomVelocity() {
        return settings.getFloat(Settings.CAMERA_ZOOM_VELOCITY.name());
    }

    public void setCameraZoomVelocity(float cameraZoomVelocity) {
        cameraZoomVelocity = Math.max(0, Math.min(1, cameraZoomVelocity));
        settings.putFloat(Settings.CAMERA_ZOOM_VELOCITY.name(), cameraZoomVelocity);
    }

    public float getMusicVolume() {
        return settings.getFloat(Settings.MUSIC_VOLUME.name());
    }

    public void setMusicVolume(float volume) {
        volume = Math.max(0, Math.min(1, volume));
        settings.putFloat(Settings.MUSIC_VOLUME.name(), volume);
        if (music != null) {
            music.setVolume(volume);
        }
    }

    public float getSoundVolume() {
        return settings.getFloat(Settings.SOUND_VOLUME.name());
    }

    public void setSoundVolume(float volume) {
        volume = Math.max(0, Math.min(1, volume));
        settings.putFloat(Settings.SOUND_VOLUME.name(), volume);
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
        FULLSCREEN, CAMERA_VELOCITY, CAMERA_ZOOM_VELOCITY, MUSIC_VOLUME, SOUND_VOLUME
    }
}

