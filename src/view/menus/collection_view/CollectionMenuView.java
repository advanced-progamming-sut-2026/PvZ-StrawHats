package view.menus.collection_view;

import controller.menus.authentication.SignupMenu;
import model.utils.Regex;
import view.menus.MenuView;

public class CollectionMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_PLANT.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_ALL_ZOMBIES.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_UPGRADE_PLANT.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_PURCHASE_PLANT.getMatcherRaw(text).matches() ||
                Regex.MENU_COLLECTION_SHOW_PLANTS.getMatcherRaw(text).matches() || Regex.MENU_COLLECTION_SHOW_ZOMBIES.getMatcherRaw(text).matches() || Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_COLLECTION_SHOW_ZOMBIE.getMatcherRaw(text).matches()){
            new SignupMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
