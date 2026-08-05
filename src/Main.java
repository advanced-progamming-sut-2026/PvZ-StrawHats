import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import controller.assets.GameAssetManager;
import model.collections.plant.PlantFactory;
import model.quests.QuestLoader;
import view.AppView;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        try {
            PlantFactory.autoInit();
            QuestLoader.loadTemplates("src/resource/Quest.json");

            GameAssetManager.getInstance().initialize();

            new Thread(() -> {
                AppView.run();
            }).start();

        } catch (Exception e) {
            Gdx.app.error("Main", "Error during initialization", e);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        try {
            GameAssetManager.getInstance().update();
        } catch (Exception e) {
        }
    }

    @Override
    public void resize(int width, int height) {
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
            GameAssetManager.getInstance().dispose();
            Gdx.app.log("Main", "Game disposed successfully.");
        } catch (Exception e) {
            Gdx.app.error("Main", "Error during dispose", e);
        }
    }
}