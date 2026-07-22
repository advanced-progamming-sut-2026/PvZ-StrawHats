package view.menus.while_match;

import controller.menus.match.MatchMenu;
import view.menus.MenuView;

public class MatchMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        new MatchMenu().handleCommand(text);
    }
}
