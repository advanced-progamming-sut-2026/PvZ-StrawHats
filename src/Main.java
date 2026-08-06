import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

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

            // Login/Signup now have real graphical screens (view.screens.*), so this is what
            // used to be `new Thread(() -> AppView.run()).start()`: instead of the console loop
            // driving model.App.currentMenu from a background Scanner thread (which would race
            // with the render thread's own reads of it), ScreenManager resolves whichever screen
            // matches App.currentMenu right now and shows it. Menus without graphics yet fall
            // back to PlaceholderScreen, which still lets you type the exact phase-1 commands.
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
