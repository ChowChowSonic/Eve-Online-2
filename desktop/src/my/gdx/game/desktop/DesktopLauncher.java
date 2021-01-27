package my.gdx.game.desktop;

import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import my.gdx.game.EveOnline2;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Toolkit.getDefaultToolkit().getScreenSize().width; 
		config.height = Toolkit.getDefaultToolkit().getScreenSize().height;
		//config.fullscreen = true; 
		config.vSyncEnabled = true;
		new LwjglApplication(new EveOnline2(), config);
	}
}
