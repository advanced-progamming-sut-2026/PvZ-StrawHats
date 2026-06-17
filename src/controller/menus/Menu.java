package controller.menus;

import model.utils.App;
import model.utils.Regex;

import java.util.regex.Matcher;

public abstract class Menu {
    public void changeMenu(String text) {
        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
        matcher.matches();
        String menu = matcher.group("menuname");
        if (menu.equals("Game Menu")) {
            App.currentMenu = new GameMenu();
        } else if (menu.equals("Profile Menu")) {
            App.currentMenu = new ProfileMenu();
        } else if (menu.equals("Setting Menu")) {
            App.currentMenu = new SettingMenu();
        } else if (menu.equals("News Menu")) {
            App.currentMenu = new NewsMenu();
        }
    }
    public abstract String getName();
    public abstract void handleCommand(String text);
    public abstract void exitMenu();
    public abstract String showMenu();
}
