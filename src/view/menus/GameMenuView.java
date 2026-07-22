package view.menus;

import controller.menus.GameMenu;

public class GameMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        new GameMenu().handleCommand(text);
    }
}
