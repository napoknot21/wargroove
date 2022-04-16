package up.wargroove.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.ui.Model;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.ui.views.scenes.MainMenu;
import up.wargroove.core.ui.views.scenes.View;

/**
 * The wargroove client.
 */
public class WargrooveClient extends Game {

    public WargrooveClient(boolean debug) {
        this.debug = debug;
    }

    public WargrooveClient() {
        this(false);
    }

    /**
     * The drawing tool.
     */
    SpriteBatch batch;
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
    private boolean debug;

    Settings settings;

    Music music;

    @Override
    public void create() {
        settings = new Settings();
        batch = new SpriteBatch();
        assets = Assets.getInstance();
        assets.loadDefault();
        assets.loadEntitiesDescription();
        Model model = new Model();
        controller = new Controller(model, this);
        controller.create();
        scene = new MainMenu(controller,controller.getModel(), this);
        controller.setScreen(scene);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        if (scene!= null) scene.dispose();
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
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

    /**
     * Puts the app in debug mode.
     *
     * @param debug if true, the app is in debug mod
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
        if (scene != null) {
            scene.setDebug(debug);
        }
    }

    /**
     * Keeps the debug mode if it was already activated.
     */
    public void setDebug() {
        setDebug(debug);
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        this.scene = (View) screen;
        setDebug();
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

    public Settings getSettings() {
        return settings;
    }

    public void playMusic() {
        if (music != null) {
            music.setVolume(settings.volume);
            music.play();
        }
    }

    public class Settings {
        private boolean sound = true;
        private float volume = 1f;
        private float cameraVelocity = 0.40f;
        private float cameraZoomVelocity = 0.40f;
        private boolean fullScreen = false;

        public boolean isSound() {
            return sound;
        }

        public void setSound(boolean sound) {
            this.sound = sound;
            if (!sound) {
                stopMusic();
            } else {
                playMusic();
            }
        }

        public float getCameraVelocity() {
            return cameraVelocity;
        }

        public void setCameraVelocity(float cameraVelocity) {
            this.cameraVelocity = cameraVelocity;
        }

        public float getCameraZoomVelocity() {
            return cameraZoomVelocity;
        }

        public void setCameraZoomVelocity(float cameraZoomVelocity) {
            this.cameraZoomVelocity = cameraZoomVelocity;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
            if (music != null) {
                music.setVolume(volume);
            }
        }

        public boolean isFullScreen() {
            return fullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
        }
    }
}
