package view.menus;

import controller.menus.LeaderboardMenu;
import model.Regex;

public class LeaderboardMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if (Regex.MENU_EXIT.getMatcherRaw(text).matches() || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            new LeaderboardMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}