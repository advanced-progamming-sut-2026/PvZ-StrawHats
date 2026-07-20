package view.menus.collection_view;

import controller.menus.CollectionMenu;
import model.Regex;
import view.menus.MenuView;

public class CollectionMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_PLANT.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_ALL_ZOMBIES.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_UPGRADE_PLANT.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_PURCHASE_PLANT.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_SHOW_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_ZOMBIES.getMatcherRaw(text).matches() || Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_COLLECTION_SHOW_ZOMBIE.getMatcherRaw(text).matches()){
            new CollectionMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
