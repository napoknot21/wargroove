package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Controller;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.views.scenes.MainMenu;
import up.wargroove.core.ui.views.scenes.View;

import javax.annotation.Nonnull;

/**
 * The wargroove client.
 */
public class WargrooveClient extends Game {

    private static final float FADING_STEP = 0.01f;

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
     * The shown scene.
     */
    private View scene;

    /**
     * The client settings.
     */
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
        assets.loadDescription();
        settings = Gdx.app.getPreferences("settings");
        loadSettings();
        batch = new SpriteBatch();
        Model model = new Model();
        Controller controller = new Controller(model, this);
        controller.create();
        scene = new MainMenu(controller, controller.getModel(), this);
        controller.setScreen(scene);
    }

    /**
     * Loads the setting from the user directory.
     */
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

    /**
     * Sets the client music
     *
     * @param music   The future music
     * @param looping indicate if the music will loop.
     */
    public void setMusic(@Nonnull Music music, boolean looping) {
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

    /**
     * Play the loaded music.
     */
    public void playMusic() {
        if (music != null) {
            music.setVolume(getMusicVolume());
            music.play();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (music.getVolume() < getMusicVolume()) {
                        music.setVolume(music.getVolume() + FADING_STEP);
                    }
                    else {
                        music.setVolume(getMusicVolume());
                        this.cancel();
                    }
                }
            },0f, FADING_STEP);
        }
    }

    /**
     * Gets the camera's velocity.
     *
     * @return the velocity in percentage.
     */
    public float getCameraVelocity() {
        return settings.getFloat(Settings.CAMERA_VELOCITY.name());
    }

    /**
     * Sets the camera's velocity.
     *
     * @param cameraVelocity The value in percentage.
     */
    public void setCameraVelocity(float cameraVelocity) {
        cameraVelocity = Math.max(0, Math.min(1, cameraVelocity));
        this.settings.putFloat(Settings.CAMERA_VELOCITY.name(), cameraVelocity);
    }

    /**
     * Gets the zoom velocity of the camera.
     *
     * @return The velocity in percentage.
     */
    public float getCameraZoomVelocity() {
        return settings.getFloat(Settings.CAMERA_ZOOM_VELOCITY.name());
    }

    /**
     * Sets the zoom velocity of the camera.
     *
     * @param cameraZoomVelocity The value in percentage.
     */
    public void setCameraZoomVelocity(float cameraZoomVelocity) {
        cameraZoomVelocity = Math.max(0, Math.min(1, cameraZoomVelocity));
        settings.putFloat(Settings.CAMERA_ZOOM_VELOCITY.name(), cameraZoomVelocity);
    }

    /**
     * Gets the music's volume.
     *
     * @return the music's volume in percentage.
     */
    public float getMusicVolume() {
        return settings.getFloat(Settings.MUSIC_VOLUME.name());
    }

    /**
     * Sets the music's volume.
     *
     * @param volume the value in percentage.
     */
    public void setMusicVolume(float volume) {
        volume = Math.max(0, Math.min(1, volume));
        settings.putFloat(Settings.MUSIC_VOLUME.name(), volume);
        if (music != null) {
            music.setVolume(volume);
        }
    }

    /**
     * Gets the sounds' volume.
     *
     * @return the sound's volume in percentage.
     */
    public float getSoundVolume() {
        return settings.getFloat(Settings.SOUND_VOLUME.name());
    }

    /**
     * Sets the sounds' volume.
     *
     * @param volume The value in percentage.
     */
    public void setSoundVolume(float volume) {
        volume = Math.max(0, Math.min(1, volume));
        settings.putFloat(Settings.SOUND_VOLUME.name(), volume);
    }

    /**
     * Indicates if the client is in full screen.
     *
     * @return true is the client is in full screen mode, false otherwise.
     */
    public boolean isFullScreen() {
        return settings.getBoolean(Settings.FULLSCREEN.name());
    }

    /**
     * Sets the full screen mode according to the argument
     *
     * @param fullScreen if true the client will be in full screen else it will be in windowed mode.
     */
    public void setFullScreen(boolean fullScreen) {
        settings.putBoolean(Settings.FULLSCREEN.name(), fullScreen);
        if (fullScreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(800, 600);
        }
    }

    /**
     * The settings' map keys.
     */
    private enum Settings {
        FULLSCREEN, CAMERA_VELOCITY, CAMERA_ZOOM_VELOCITY, MUSIC_VOLUME, SOUND_VOLUME
    }
}

