package controller.menus;

import controller.menus.Menu;
import controller.menus.GameMenu;
import model.App;
import model.Regex;
import java.util.regex.Matcher;

public class TravelLogMenu extends Menu {
    private String currentPage = "DAILY";

    @Override
    public String getName() {
        return "Travel Log Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.TRAVEL_LOG_PAGE.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.TRAVEL_LOG_PAGE.getMatcherRaw(text);
            matcher.matches();
            currentPage = matcher.group("pagename");
            // *
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            System.out.println("Invalid command.");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "Travel Log - Page: " + currentPage;
    }
}