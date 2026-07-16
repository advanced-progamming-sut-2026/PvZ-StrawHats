package view.menus;

import controller.menus.NewsMenu;
import model.Regex;

public class NewsMenuView extends MenuView {
    @Override
    public void showMenu(String text) {
        if (Regex.MENU_NEWS_SHOW_ALL.getMatcherRaw(text).matches() ||
                Regex.MENU_NEWS_SHOW_UNREAD.getMatcherRaw(text).matches() ||
                Regex.MENU_ENTER.getMatcherRaw(text).matches() ||
                Regex.MENU_EXIT.getMatcherRaw(text).matches() ||
                Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            new NewsMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}