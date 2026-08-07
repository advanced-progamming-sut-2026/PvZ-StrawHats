import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;

import controller.assets.GameAssetManager;
import controller.assets.ScreenManager;
import model.collections.plant.PlantFactory;
import model.quests.QuestLoader;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        try {
            PlantFactory.autoInit();
            QuestLoader.loadTemplates("src/resource/Quest.json");

            GameAssetManager.get().initialize();
            ScreenManager.syncWithCurrentMenu();

        } catch (Exception e) {
            Gdx.app.error("Main", "Error during initialization", e);
        }
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            toggleFullscreen();
        }

        try {
            GameAssetManager.get().update();
        } catch (Exception e) {
        }

        ScreenManager.render(Gdx.graphics.getDeltaTime());
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1152, 648);
        } else {
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        try {
            ScreenManager.dispose();
            GameAssetManager.get().dispose();
            Gdx.app.log("Main", "Game disposed successfully.");
        } catch (Exception e) {
            Gdx.app.error("Main", "Error during dispose", e);
        }
    }
}