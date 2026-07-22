package view.menus;

import controller.menus.MainMenu;

public class MainMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        new MainMenu().handleCommand(text);
    }
}
