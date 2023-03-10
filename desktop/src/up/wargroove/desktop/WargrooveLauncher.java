package up.wargroove.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import up.wargroove.core.WargrooveClient;

import java.awt.*;

/**
 * The game launcher.
 */
public class WargrooveLauncher {

    /**
     * The main entry of the software.
     */
    public static void main(String[] args) {
        boolean debug;
        if (args.length != 0 && args[0].startsWith("--debug=")) {
            debug = args[0].split("=")[1].equalsIgnoreCase("on");
        } else {
            debug = false;
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Wargroove");
        config.setWindowSizeLimits(800, 600, -1, -1);
        EventQueue.invokeLater(() -> new Lwjgl3Application(new WargrooveClient(debug), config));

    }

}
