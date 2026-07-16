package controller.menus;

import model.App;
import model.Regex;
import model.match.main.levels.Level;
import model.user_data.User;
import model.utils.LevelLoader;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardMenu extends Menu {

    @Override
    public String getName() {
        return "Leaderboard Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            System.out.println("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        List<Level> allLevels;
        try {
            allLevels = LevelLoader.loadLevels("/Levels.json");
        } catch (Exception e) {
            return "Error: could not load levels.";
        }

        List<User> ranked = User.users.stream()
                .filter(u -> u.userState.lastLevel > 0)
                .sorted(Comparator.comparingInt((User u) -> u.userState.lastLevel).reversed())
                .collect(Collectors.toList());

        if (ranked.isEmpty()) return "No records yet.";

        StringBuilder sb = new StringBuilder();
        for (User u : ranked) {
            sb.append(u.nickname).append(" - ").append(formatProgress(u.userState.lastLevel, allLevels)).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatProgress(int lastLevelId, List<Level> allLevels) {
        Level level = allLevels.stream().filter(l -> l.getId() == lastLevelId).findFirst().orElse(null);
        if (level == null) return "Stage ?";

        List<Level> seasonLevels = allLevels.stream()
                .filter(l -> l.getSeason().getName().equalsIgnoreCase(level.getSeason().getName()))
                .sorted(Comparator.comparingInt(Level::getId))
                .collect(Collectors.toList());

        int stageNumber = seasonLevels.indexOf(level) + 1;
        return "Stage " + stageNumber + " " + level.getSeason().getName();
    }
}