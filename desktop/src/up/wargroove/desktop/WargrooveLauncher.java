package up.wargroove.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import up.wargroove.core.WargrooveClient;

/**
 * The game launcher.
 */
public class WargrooveLauncher {

    /**
     * The main entry of the software.
     */
    public static void main(String[] args) {
        try {
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setTitle("Wargroove");
            new Lwjgl3Application(new WargrooveClient(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
