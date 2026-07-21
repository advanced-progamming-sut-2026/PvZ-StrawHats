package view.menus.while_match;

import controller.menus.match.MeanwhileMenu;
import model.Regex;
import view.menus.MenuView;

import java.util.regex.Pattern;

public class MeanwhileMatchView extends MenuView {
    private static final Pattern PAUSE = Pattern.compile("^\\s*pause\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RESUME = Pattern.compile("^\\s*resume\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RESTART = Pattern.compile("^\\s*restart\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_GAME = Pattern.compile("^\\s*end\\s+game\\s+-r\\s+(?<r>\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

    @Override
    public void showMenu(String text) {
        if (PAUSE.matcher(text.trim()).matches() || RESUME.matcher(text.trim()).matches()
                || RESTART.matcher(text.trim()).matches() || END_GAME.matcher(text.trim()).matches()
                || Regex.MENU_EXIT.getMatcherRaw(text).matches() || Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()
                || Regex.ADVANCE_TIME.getMatcherRaw(text).matches() || Regex.SHOW_MAP.getMatcherRaw(text).matches()
                || Regex.SHOW_SUN_AMOUNT.getMatcherRaw(text).matches() || Regex.SHOW_PLANT_STATUS.getMatcherRaw(text).matches()
                || Regex.SHOW_TILE_STATUS.getMatcherRaw(text).matches() || Regex.ZOMBIES_INFO.getMatcherRaw(text).matches()
                || Regex.COLLECT_SUN.getMatcherRaw(text).matches()
                || Regex.PLANT_ON_FIELD.getMatcherRaw(text).matches() || Regex.PLUCK_PLANT_FIELD.getMatcherRaw(text).matches()
                || Regex.FEED_PLANT_FIELD.getMatcherRaw(text).matches() || Regex.CHEAT_ADD_SUNS.getMatcherRaw(text).matches()
                || Regex.CHEAT_ADD_PLANT_FOOD.getMatcherRaw(text).matches() || Regex.CHEAT_REMOVE_COOLDOWN.getMatcherRaw(text).matches()
                || Regex.CHEAT_SPAWN_ZOMBIE.getMatcherRaw(text).matches() || Regex.RELEASE_THE_NUKE.getMatcherRaw(text).matches()) {
            new MeanwhileMenu().handleCommand(text);
        } else {
            System.out.println("Not Valid");
        }
    }
}