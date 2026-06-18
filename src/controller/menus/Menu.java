package controller.menus;

import controller.menus.authentication.SignupMenu;
import model.utils.App;
import model.utils.Regex;

import java.util.regex.Matcher;

public abstract class Menu {
    public void changeMenu(String text) {
        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
        matcher.matches();
        String menu = matcher.group("menuname");
        switch (menu) {
            case "Game Menu" -> App.currentMenu = new GameMenu();
            case "Profile Menu" -> App.currentMenu = new ProfileMenu();
            case "Setting Menu" -> App.currentMenu = new SettingMenu();
            case "News Menu" -> App.currentMenu = new NewsMenu();
            case "SignUp Menu" -> App.currentMenu = new SignupMenu();
            case "Main Menu" -> App.currentMenu = new MainMenu();
        }
    }
    public abstract String getName();
    public abstract void handleCommand(String text);
    public abstract void exitMenu();
    public abstract String showMenu();
}
