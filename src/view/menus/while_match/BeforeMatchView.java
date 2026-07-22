package view.menus.while_match;

import controller.menus.match.BeforeMenu;
import view.menus.MenuView;

public class BeforeMatchView extends MenuView {
    @Override
    public void showMenu(String text) {
        new BeforeMenu().handleCommand(text);
    }
}
