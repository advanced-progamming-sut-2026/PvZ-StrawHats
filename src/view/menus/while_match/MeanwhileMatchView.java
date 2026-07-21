package view.menus.while_match;

import controller.menus.match.Meanwhile;
import model.Regex;
import view.menus.MenuView;

import java.util.regex.Pattern;

public class MeanwhileMatchView extends MenuView {
    private static final Pattern PAUSE = Pattern.compile("^\\s*pause\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RESUME = Pattern.compile("^\\s*resume\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RESTART = Pattern.compile("^\\s*restart\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern END_GAME = Pattern.compile("^\\s*end\\s+game\\s+-r\\s+(?<result>\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

    @Override
    public void showMenu(String text) {

    }
}
