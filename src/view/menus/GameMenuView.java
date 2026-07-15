package view.menus;

import controller.menus.GameMenu;
import model.Regex;

public class GameMenuView extends MenuView{
    @Override
    public void showMenu(String text) {
        if(Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text).matches() || Regex.MENU_GREENHOUSE.getMatcherRaw(text).matches() ||
                Regex.MENU_TRAVEL_LOG.getMatcherRaw(text).matches()|| Regex.MENU_COIN_WALLET.getMatcherRaw(text).matches() ||
                Regex.MENU_LEADERBOARD.getMatcherRaw(text).matches() || Regex.MENU_GEM_WALLET.getMatcherRaw(text).matches()
        || Regex.MENU_ENTER.getMatcherRaw(text).matches() || Regex.MENU_EXIT.getMatcherRaw(text).matches()
                || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()){
            // in bakhsh ye ceat code ham dare ke felan nazadam ta badan ba baghi
            //cheat code ha bezanim
            new GameMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}
