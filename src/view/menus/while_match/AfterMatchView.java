package view.menus.while_match;

import controller.menus.match.After;
import model.Regex;
import view.menus.MenuView;

public class AfterMatchView extends MenuView {
    @Override
    public void showMenu(String text) {
        if (Regex.MENU_EXIT.getMatcherRaw(text).matches() || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            new After().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
