package controller.menus;

import model.App;
import model.Regex;
import model.match.main.levels.Level;
import model.user_data.User;
import model.utils.LevelLoader;
import view.GeneralPrinter;

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
            GeneralPrinter.print(showMenu());
        } else {
            GeneralPrinter.print("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        String commands = "\nCommands:\n  menu exit | menu show current";
        List<Level> allLevels;
        try {
            allLevels = LevelLoader.loadLevels();
        } catch (Exception e) {
            return "[ Leaderboard Menu ]\nError: could not load levels." + commands;
        }

        List<User> ranked = User.users.stream()
                .filter(u -> u.userState.lastLevel > 0)
                .sorted(Comparator.comparingInt((User u) -> u.userState.lastLevel).reversed())
                .collect(Collectors.toList());

        if (ranked.isEmpty()) return "[ Leaderboard Menu ]\nNo records yet." + commands;

        StringBuilder sb = new StringBuilder("[ Leaderboard Menu ]\n");
        for (User u : ranked) {
            sb.append(u.nickname).append(" - ").append(formatProgress(u.userState.lastLevel, allLevels)).append("\n");
        }
        return sb.toString().trim() + commands;
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
