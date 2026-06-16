package view.menus;

import controller.menus.authentication.SignupMenu;
import model.utils.Regex;

public class SignupMenuView extends MenuView {


    @Override
    public void showMenu(String text) {
        if(Regex.REGISTER.getMatcherRaw(text).matches() || Regex.PICK_QUESTION.getMatcherRaw(text).matches() ||
                Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
        || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            new SignupMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }

}
