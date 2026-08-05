import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import controller.assets.GameAssetManager;
import model.collections.plant.PlantFactory;
import model.quests.QuestLoader;
import view.AppView;

public class Main extends ApplicationAdapter {

    @Override
    public void create() {
        PlantFactory.autoInit();
        QuestLoader.loadTemplates("src/resource/Quest.json");
        GameAssetManager.getInstance().initialize();
        AppView.run();
    }

    @Override
    public void render() {
        GameAssetManager.getInstance().update();
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
        GameAssetManager.getInstance().dispose();
        Gdx.app.log("Main", "Game disposed successfully.");
    }
}