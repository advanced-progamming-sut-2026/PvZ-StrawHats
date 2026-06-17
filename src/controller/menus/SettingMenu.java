package controller.menus;

import model.utils.Regex;

public class SettingMenu implements Menu{

    @Override
    public void changeMenu(String text) {

    }

    @Override
    public String getName() {
        return "Setting Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_SETTINGS_CHANGE_DIFFICULTY.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }

    @Override
    public void exitMenu() {

    }

    @Override
    public String showMenu() {
        return "";
    }


}
