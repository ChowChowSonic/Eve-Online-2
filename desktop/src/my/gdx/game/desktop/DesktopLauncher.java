package my.gdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import my.gdx.game.EveOnline2;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 2560; 
		config.height = 1440;
		config.fullscreen = true; 
		config.vSyncEnabled = true;
		new LwjglApplication(new EveOnline2(), config);
	}
}
