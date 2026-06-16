package view.menus;

import controller.menus.SettingMenu;
import model.utils.Regex;

public class SettingMenuView extends MenuView{
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_SETTINGS_CHANGE_DIFFICULTY.getMatcherRaw(text).matches() ||
                Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            new SettingMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
