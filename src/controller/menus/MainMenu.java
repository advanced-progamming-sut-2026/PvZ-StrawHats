package controller.menus;

import controller.menus.authentication.LoginMenu;
import model.utils.App;
import model.utils.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainMenu extends Menu{

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

