package view.menus;

import controller.menus.MainMenu;
import model.Regex;

public class MainMenuView extends MenuView{
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_LOGOUT.getMatcherRaw(text).matches()|| Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            new MainMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
