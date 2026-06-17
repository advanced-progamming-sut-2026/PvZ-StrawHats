package controller.menus.authentication;

import controller.menus.Menu;
import model.utils.Regex;

public class SignupMenu implements Menu {

    boolean isAlreadyRegisterd = false;

    @Override
    public void changeMenu(String text) {

    }

    @Override
    public String getName() {
        return "SignUP Menu";
    }

    @Override
    public void handleCommand(String text) {
        if(Regex.REGISTER.getMatcherRaw(text).matches()) {

        } else if (Regex.PICK_QUESTION.getMatcherRaw(text).matches()) {

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
