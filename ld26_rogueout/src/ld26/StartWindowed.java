package ld26;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class StartWindowed {
	private static final int SCREEN_WIDTH = 800;
	private static final int SCREEN_HEIGHT = 600;
	
    public static void main(String[] args) {  
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();  
  
        config.title = "Rogueout (Ludum Dare 26)";
        config.width = SCREEN_WIDTH;
        config.height = SCREEN_HEIGHT;
        config.fullscreen = false;
        config.useGL20 = false;
        config.useCPUSynch = true;  
        config.forceExit = true;
        config.vSyncEnabled = true;
  
        new LwjglApplication(new RogueOut(SCREEN_WIDTH, SCREEN_HEIGHT), config);  
    }  
}
