package view.menus.while_match;

import controller.menus.match.MeanwhileMenu;
import view.menus.MenuView;

public class MeanwhileMatchView extends MenuView {
    @Override
    public void showMenu(String text) {
        new MeanwhileMenu().handleCommand(text);
    }
}
