package controller.assets;

import view.general_screens.BaseScreen;

/**
 * Static owner of "which screen is currently showing". Screens call
 * {@code ScreenManager.setScreen(new SomeScreen())} to navigate; {@code Main} just forwards
 * render/resize/dispose here every frame.
 */
public final class ScreenManager {

    private static BaseScreen currentScreen;

    private ScreenManager() {
    }

    public static void setScreen(BaseScreen next) {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }
        currentScreen = next;
        if (currentScreen != null) {
            currentScreen.show();
        }
    }

    public static BaseScreen getScreen() {
        return currentScreen;
    }

    public static void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }

    public static void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    public static void dispose() {
        if (currentScreen != null) {
            currentScreen.dispose();
            currentScreen = null;
        }
    }
}
