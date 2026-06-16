package controller.menus.match;

import controller.menus.Menu;
import model.utils.Regex;

public class Before implements Menu {

    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "Before Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.SHOW_AVAILABLE_PLANTS.getMatcherRaw(text).matches()) {

        } else if (Regex.SHOW_ALL_PLANTS.getMatcherRaw(text).matches()) {

        } else if (Regex.ADD_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.REMOVE_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.BOOST_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.START_GAME.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }
}
