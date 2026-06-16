package view.menus;

import controller.menus.authentication.LoginMenu;
import model.utils.Regex;

public class LoginMenuView extends MenuView {

    @Override
    public void showMenu(String text) {
        if(Regex.LOGIN.getMatcherRaw(text).matches() || Regex.FORGET_PASSWORD.getMatcherRaw(text).matches() ||
                Regex.ANSWER.getMatcherRaw(text).matches() || Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            new LoginMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
