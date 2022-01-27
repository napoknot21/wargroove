package up.wargroove.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;

import up.wargroove.core.WargrooveClient;

public class WargrooveLauncher {

	public static void main(String [] args) {

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Wargroove");
		new Lwjgl3Application(new WargrooveClient(), config);

	}

}
