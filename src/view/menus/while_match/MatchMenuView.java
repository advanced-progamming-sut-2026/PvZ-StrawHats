package view.menus.while_match;

import controller.menus.match.MatchMenu;
import model.Regex;
import view.menus.MenuView;

public class MatchMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if (Regex.START_GAME.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            new MatchMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}