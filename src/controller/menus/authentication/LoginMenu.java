package controller.menus.authentication;

import controller.menus.Menu;
import model.utils.Regex;

public class LoginMenu implements Menu {

    @Override
    public void changeMenu(String text) {

    }

    @Override
    public String getName() {
        return "Login Menu";
    }

    @Override
    public void handleCommand(String text) {
        if(Regex.LOGIN.getMatcherRaw(text).matches()) {

        } else if (Regex.FORGET_PASSWORD.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        } else if (Regex.ANSWER.getMatcherRaw(text).matches()) {

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
