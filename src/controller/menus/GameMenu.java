package controller.menus;

import model.App;
import model.Regex;
import java.util.regex.Matcher;

public class GameMenu extends Menu {

    @Override
    public String getName() {
        return "Game Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text);
            matcher.matches();
            String chapterName = matcher.group("chaptername");
            // *
        } else if (Regex.MENU_TRAVEL_LOG.getMatcherRaw(text).matches()) {
            App.currentMenu = new TravelLogMenu();
        } else if (Regex.MENU_COIN_WALLET.getMatcherRaw(text).matches()) {
            // *
        } else if (Regex.MENU_GEM_WALLET.getMatcherRaw(text).matches()) {
            // *
        } else if (Regex.MENU_LEADERBOARD.getMatcherRaw(text).matches()) {
            // *
        } else if (Regex.MENU_GREENHOUSE.getMatcherRaw(text).matches()) {
            App.currentMenu = new controller.menus.greenhouse.GreenhouseController();
        } else if (Regex.MENU_CHEAT_ADD.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_CHEAT_ADD.getMatcherRaw(text);
            matcher.matches();
            int amount = Integer.parseInt(matcher.group("n"));
            String type = matcher.group("r");
            if (type.equals("coin")) {
                // *
            } else if (type.equals("diamond")) {
                // *
            }
        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
            matcher.matches();
            String menuName = matcher.group("menuname");
            if (menuName.equals("Collection Menu")) {
                // *
            }
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new MainMenu();
    }

    @Override
    public String showMenu() {
        return "Game Menu";
    }
}