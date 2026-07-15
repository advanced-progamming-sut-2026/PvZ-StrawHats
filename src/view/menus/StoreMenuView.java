package view.menus;

import controller.menus.store.StoreController;

public class StoreMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        new StoreController().handleCommand(text);
    }
}
