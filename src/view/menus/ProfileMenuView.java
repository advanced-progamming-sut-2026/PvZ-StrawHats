package view.menus;

import controller.menus.ProfileMenu;
import model.utils.Regex;

public class ProfileMenuView extends MenuView{
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_PROFILE_CHANGE_EMAIL.getMatcherRaw(text).matches() || Regex.MENU_PROFILE_CHANGE_NICKNAME.getMatcherRaw(text).matches() ||
                Regex.MENU_PROFILE_CHANGE_PASSWORD.getMatcherRaw(text).matches() || Regex.MENU_ENTER.getMatcherRaw(text).matches() ||
                Regex.MENU_PROFILE_SHOW_INFO.getMatcherRaw(text).matches() || Regex.MENU_PROFILE_CHANGE_USERNAME.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            new ProfileMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
