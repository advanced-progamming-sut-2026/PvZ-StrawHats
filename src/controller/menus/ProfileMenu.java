package controller.menus;

import model.utils.Regex;

public class ProfileMenu implements Menu{

    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "Profile Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_PROFILE_SHOW_INFO.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_PROFILE_CHANGE_USERNAME.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_PROFILE_CHANGE_PASSWORD.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_PROFILE_CHANGE_NICKNAME.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_PROFILE_CHANGE_EMAIL.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }


}
