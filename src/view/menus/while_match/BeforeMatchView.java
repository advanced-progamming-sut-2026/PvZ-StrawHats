package view.menus.while_match;

import controller.menus.match.Before;
import model.App;
import model.Regex;
import view.menus.MenuView;

public class BeforeMatchView extends MenuView {
    @Override
    public void showMenu(String text) {
        if(Regex.SHOW_ALL_PLANTS.getMatcherRaw(text).matches() || Regex.REMOVE_PLANT.getMatcherRaw(text).matches() ||
                Regex.START_GAME.getMatcherRaw(text).matches() || Regex.ADD_PLANT.getMatcherRaw(text).matches() || Regex.SHOW_AVAILABLE_PLANTS.getMatcherRaw(text).matches() ||
                Regex.BOOST_PLANT.getMatcherRaw(text).matches() || Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            if (App.currentMenu instanceof Before before) {
                before.handleCommand(text);
            } else {
                System.out.println("Not currently in a pre-match loadout screen.");
            }
        } else {
            System.out.println("Not Valid");
        }
    }
}
