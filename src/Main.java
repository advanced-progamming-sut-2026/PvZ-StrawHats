import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        try {
            GameAssetManager.get().update();
        } catch (Exception e) {
        }

        ScreenManager.render(Gdx.graphics.getDeltaTime());
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
