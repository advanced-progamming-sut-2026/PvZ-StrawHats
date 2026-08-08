package controller.assets;

import controller.menus.*;
import controller.menus.authentication.LoginMenu;
import controller.menus.authentication.SignupMenu;
import model.App;
import view.general_screens.BaseScreen;
import view.screens.*;

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
        if (menu instanceof MainMenu) {
            return new MainMenuScreen();
        }
        if (menu instanceof ProfileMenu) {
            return new ProfileMenuScreen();
        }
        if (menu instanceof NewsMenu) {
            return new NewsScreen();
        }
        if (menu instanceof TravelLogMenu) {
            return new TravelLogScreen();
        }
        if (menu instanceof SettingMenu) {
            return new SettingsScreen();
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