package view.menus.while_match;

import controller.menus.match.AfterMenu;
import view.menus.MenuView;

public class AfterMatchView extends MenuView {
    @Override
    public void showMenu(String text) {
        new AfterMenu().handleCommand(text);
    }
}
