package controller.menus;

import model.utils.Regex;

public class MainMenu implements Menu{
    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "Main Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_LOGOUT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }
}
