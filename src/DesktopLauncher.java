import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
public class DesktopLauncher {
    public static void main(String[] args) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Plants vs Zombies - Straw Hats");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        config.setForegroundFPS(60);

        config.setWindowIcon(
                Files.FileType.Internal,
                "images/ui/icon.png",
                "images/ui/icon.png"
        );

        new Lwjgl3Application(new Main(), config);
    }


}