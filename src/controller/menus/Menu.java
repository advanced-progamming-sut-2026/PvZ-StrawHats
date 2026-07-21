package controller.menus;

import controller.menus.authentication.LoginMenu;
import controller.menus.authentication.SignupMenu;
import model.App;
import model.Regex;
import view.GeneralPrinter;

import java.util.regex.Matcher;

public abstract class Menu {
    public void changeMenu(String text) {
        String menuKey;

        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
        if (matcher.matches()) {
            menuKey = matcher.group("menuname");
        } else {
            menuKey = text;
        }

        String normalized = menuKey.toLowerCase().replace("menu", "").trim();

        switch (normalized) {
            case "game" -> App.currentMenu = new GameMenu();
            case "profile" -> App.currentMenu = new ProfileMenu();
            case "settings", "setting" -> App.currentMenu = new SettingMenu();
            case "news" -> App.currentMenu = new NewsMenu();
            case "signup" -> App.currentMenu = new SignupMenu();
            case "main" -> App.currentMenu = new MainMenu();
            case "login" -> App.currentMenu = new LoginMenu();
            case "collection" -> App.currentMenu = new CollectionMenu();
            case "travellog" -> App.currentMenu = new TravelLogMenu();
            case "network" -> App.currentMenu = new NetworkMenu();
            default -> GeneralPrinter.print("Error: no such menu.");
        }
    }

    public void getInput() {

    }
    public abstract String getName();
    public abstract void handleCommand(String text);
    public abstract void exitMenu();
    public abstract String showMenu();
}
