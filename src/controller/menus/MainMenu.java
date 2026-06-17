package controller.menus;

import controller.menus.authentication.LoginMenu;
import model.utils.App;
import model.utils.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu implements Menu{
    @Override
    public void changeMenu(String s) {
        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(s);
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

    @Override
    public String getName() {
        return "Main Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_LOGOUT.getMatcherRaw(text).matches()) {
            Logout();
        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            changeMenu(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            showMenu();
        }
    }

    @Override
    public void exitMenu() {
        Logout();
    }

    @Override
    public String  showMenu() {
        return this.getName();
    }

    public void Logout() {
        App.currentMenu = new LoginMenu();
        App.currentUser = null;
    }

}

