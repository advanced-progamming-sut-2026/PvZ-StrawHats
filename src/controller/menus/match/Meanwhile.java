package controller.menus.match;

import controller.menus.GameMenu;
import controller.menus.Menu;
import model.Regex;
import model.App;
import model.utils.GameSession;

public class Meanwhile extends Menu {

    @Override
    public String getName() {
        return "Meanwhile Menu";
    }

    @Override
    public void handleCommand(String text) {
        String trimmed = text.trim().toLowerCase();
        if (trimmed.equals("pause")) {
            System.out.println(showMenu());
        } else if (trimmed.equals("resume")) {
            System.out.println("Resuming match...");
        } else if (trimmed.equals("restart")) {
            restartMatch();
        } else if (trimmed.startsWith("end game")) {
            finishMatch(trimmed.replace("end game", "").replace("-r", "").trim());
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            System.out.println("Not Valid");
        }
    }

    private void restartMatch() {
        GameSession session = GameSession.getInstance();
        if (session.getLevel() == null) {
            System.out.println("Error: no active match.");
            return;
        }
        GameSession fresh = new GameSession(session.getRows(), session.getCols());
        fresh.setLevel(session.getLevel());
        fresh.startWaves();
        System.out.println("Match restarted.");
    }

    private void finishMatch(String result) {
        boolean won = result.equalsIgnoreCase("win");
        After.reset(won);
        App.currentMenu = new After();
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "Paused - Options: resume | restart | menu exit | menu show current";
    }
}
