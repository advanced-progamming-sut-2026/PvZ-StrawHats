package controller.menus;

import controller.menus.greenhouse.GreenhouseMenu;
import model.App;
import model.Regex;
import model.match.main.levels.Level;
import model.match.main.season.Season;
import model.match.main.season.SeasonFactory;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.LevelLoader;
import view.GeneralPrinter;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

public class GameMenu extends Menu {

    @Override
    public String getName() {
        return "Game Menu";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;

        if (Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_ENTER_CHAPTER.getMatcherRaw(text);
            matcher.matches();
            enterChapter(matcher.group("chaptername"));

        } else if (Regex.MENU_TRAVEL_LOG.getMatcherRaw(text).matches()) {
            App.currentMenu = new TravelLogMenu();

        } else if (Regex.MENU_COIN_WALLET.getMatcherRaw(text).matches()) {
            GeneralPrinter.print("Coins: " + User.currentUser.userState.coins);

        } else if (Regex.MENU_GEM_WALLET.getMatcherRaw(text).matches()) {
            GeneralPrinter.print("Gems: " + User.currentUser.userState.diamonds);

        } else if (Regex.MENU_LEADERBOARD.getMatcherRaw(text).matches()) {
            App.currentMenu = new LeaderboardMenu();

        } else if (Regex.MENU_GREENHOUSE.getMatcherRaw(text).matches()) {
            App.currentMenu = new GreenhouseMenu();

        } else if (Regex.MENU_CHEAT_ADD.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_CHEAT_ADD.getMatcherRaw(text);
            matcher.matches();
            int amount = Integer.parseInt(matcher.group("n"));
            String type = matcher.group("r");

            if (type.equals("coin")) {
                User.currentUser.userState.coins += amount;
            } else if (type.equals("diamond")) {
                User.currentUser.userState.diamonds += amount;
            }

        }

         else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();

        } else {
            GeneralPrinter.print("Not Valid. Type 'menu show current' to see the commands available here.");
        }
    }

    private void enterChapter(String chapterName) {
        Season season;
        try {
            season = SeasonFactory.create(chapterName);
        } catch (IllegalArgumentException e) {
            GeneralPrinter.print("Error: no such chapter.");
            return;
        }

        List<Level> allLevels;
        try {
            allLevels = LevelLoader.loadLevels("src/resource/Levels.json");
        } catch (Exception e) {
            GeneralPrinter.print("Error: could not load levels.");
            return;
        }
        allLevels.sort(Comparator.comparingInt(Level::getId));

        UserState state = User.currentUser.userState;
        int unlockedIndex = 0;
        for (int i = 0; i < allLevels.size(); i++) {
            if (allLevels.get(i).getId() == state.lastLevel) {
                unlockedIndex = i + 1;
                break;
            }
        }

        Level target = null;
        for (int i = 0; i < allLevels.size(); i++) {
            Level lvl = allLevels.get(i);
            if (!lvl.getSeason().getName().equalsIgnoreCase(season.getName())) continue;
            if (i > unlockedIndex) break;
            if (lvl.getId() > state.lastLevel) {
                target = lvl;
                break;
            }
        }

        if (target == null) {
            GeneralPrinter.print("Error: chapter is locked.");
        } else {
            controller.menus.match.MatchMenu.selectedLevel = target;
            App.currentMenu = new controller.menus.match.MatchMenu();
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new MainMenu();
    }

    @Override
    public String showMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ Game Menu ]\n");
        sb.append("Commands:\n");
        sb.append("  menu enter chapter -c <chaptername>   (open/continue that chapter's next stage)\n");
        sb.append("  greenhouse menu\n");
        sb.append("  travel-log menu\n");
        sb.append("  menu leaderboard\n");
        sb.append("  coin-wallet menu\n");
        sb.append("  gem-wallet menu\n");
        sb.append("  menu cheat add <n> <coin/diamond>\n");
        sb.append("  menu enter collection\n");
        sb.append("  menu exit\n");
        sb.append("  menu show current");
        return sb.toString();
    }
}