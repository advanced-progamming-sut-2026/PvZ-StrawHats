package controller.menus;

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
    public void handleCommand(String text) {
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
            App.currentMenu = new controller.menus.greenhouse.GreenhouseController();

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

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
            matcher.matches();
            String menuName = matcher.group("menuname");

            if (menuName.equals("Collection Menu")) {
                App.currentMenu = new CollectionMenu();
            } else {
                changeMenu(text);
            }

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(showMenu());

        } else {
            GeneralPrinter.print("Not Valid");
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
            allLevels = LevelLoader.loadLevels("/Levels.json");
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
        return getName();
    }
}