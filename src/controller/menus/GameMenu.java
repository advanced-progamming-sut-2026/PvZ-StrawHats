package controller.menus;

import model.utils.Regex;

public class GameMenu implements Menu{

    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "Game Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_TRAVEL_LOG.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COIN_WALLET.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_GEM_WALLET.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_LEADERBOARD.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_GREENHOUSE.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }

}
