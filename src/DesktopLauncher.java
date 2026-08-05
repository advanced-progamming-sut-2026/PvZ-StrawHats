import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setTitle("Plants vs Zombies");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new Main(), config);
    }
}