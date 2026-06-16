package controller.menus;

import model.utils.Regex;

public class NewsMenu implements Menu{

    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "News Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_NEWS_SHOW_UNREAD.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_NEWS_SHOW_ALL.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }


}
