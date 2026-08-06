package controller.assets;

import controller.menus.Menu;
import controller.menus.authentication.LoginMenu;
import controller.menus.authentication.SignupMenu;
import model.App;
import view.general_screens.BaseScreen;
import view.screens.LoginScreen;
import view.screens.PlaceholderScreen;
import view.screens.SignupScreen;

/**
 * Static owner of "which screen is currently showing". Screens call
 * {@code ScreenManager.setScreen(new SomeScreen())} to navigate; {@code Main} just forwards
 * render/resize/dispose here every frame.
 */
public final class ScreenManager {

    private static BaseScreen currentScreen;
    private static Class<? extends Menu> currentMenuClass;

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

    /**
     * Keeps whichever screen is showing in sync with {@code App.currentMenu} after a
     * controller call that might have changed it (e.g. a successful login or "menu exit").
     * Only swaps screens when the underlying Menu's concrete class actually changed, so an
     * in-progress screen (a Login screen mid password-reset, say) isn't torn down - and its
     * locally-tracked wizard step lost - just because the same menu rejected one bad attempt.
     */
    public static void syncWithCurrentMenu() {
        Menu menu = App.currentMenu;
        Class<? extends Menu> menuClass = menu == null ? null : menu.getClass();
        if (currentScreen != null && menuClass == currentMenuClass) {
            return;
        }
        currentMenuClass = menuClass;
        setScreen(resolveScreen(menu));
    }

    private static BaseScreen resolveScreen(Menu menu) {
        if (menu instanceof SignupMenu) {
            return new SignupScreen();
        }
        if (menu instanceof LoginMenu) {
            return new LoginScreen();
        }
        return new PlaceholderScreen(menu);
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
