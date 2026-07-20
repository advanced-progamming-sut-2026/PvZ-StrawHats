package controller.menus.match;

import controller.menus.GameMenu;
import controller.menus.Menu;
import model.App;
import model.Regex;
import model.match.main.levels.Level;
import model.utils.GameSession;

public class MatchMenu extends Menu {
    public static Level selectedLevel;

    @Override
    public String getName() {
        return "Match Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.START_GAME.getMatcherRaw(text).matches()) {
            GameSession session = new GameSession(selectedLevel.getRows(), selectedLevel.getCols());
            session.setLevel(selectedLevel);
            Before.selectedPlants.clear();
            App.currentMenu = new Before();
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            System.out.println("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "Chapter: " + selectedLevel.getSeason().getName() + " | Stage: " + selectedLevel.getName();
    }
}
