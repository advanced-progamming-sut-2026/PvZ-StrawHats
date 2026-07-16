package view.menus;

import controller.menus.TravelLogMenu;
import model.Regex;

public class TravelLogMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if (Regex.TRAVEL_LOG_PAGE.getMatcherRaw(text).matches() ||
                Regex.MENU_EXIT.getMatcherRaw(text).matches() ||
                Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

            new TravelLogMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}