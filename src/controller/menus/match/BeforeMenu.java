package controller.menus.match;

import controller.CollectionManager;
import controller.menus.Menu;

import model.App;
import model.Regex;
import model.collections.plant.PlantJsonParser;
import model.match.main.levels.Level;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class BeforeMenu extends Menu {
    private static final int PLANT_SLOTS = 8;
    public static List<String> selectedPlants = new ArrayList<>();

    private final CollectionManager manager = new CollectionManager();

    @Override
    public String getName() {
        return "Before Menu";
    }

    @Override
    public void handleCommand(String text){
    super.handleCommand(text);


        if (Regex.SHOW_ALL_PLANTS.getMatcherRaw(text).matches()) {
            showAllPlants();
        } else if (Regex.SHOW_AVAILABLE_PLANTS.getMatcherRaw(text).matches()) {
            showAvailablePlants();
        } else if (Regex.ADD_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.ADD_PLANT.getMatcherRaw(text);
            matcher.matches();
            addPlant(matcher.group("type"));
        } else if (Regex.REMOVE_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.REMOVE_PLANT.getMatcherRaw(text);
            matcher.matches();
            removePlant(matcher.group("type"));
        } else if (Regex.BOOST_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.BOOST_PLANT.getMatcherRaw(text);
            matcher.matches();
            boostPlant(matcher.group("type"));
        } else if (Regex.START_GAME.getMatcherRaw(text).matches()) {
            startMatch();
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else {
            System.out.println("Not Valid");
        }
    }

    private Level currentLevel() {
        return GameSession.getInstance().getLevel();
    }

    private void showAllPlants() {
        UserState state = User.currentUser.userState;
        for (PlantJsonParser.PlantConfig config : manager.getUnlockedPlants(state)) {
            System.out.println(manager.formatPlant(config, true, state.getPlantLevel(config.id)));
        }
    }

    private void showAvailablePlants() {
        Level level = currentLevel();
        if (level == null || level.getAvailablePlants() == null) {
            System.out.println("Error: no level loaded.");
            return;
        }
        UserState state = User.currentUser.userState;
        for (String name : level.getAvailablePlants()) {
            PlantJsonParser.PlantConfig config = manager.findPlant(name);
            if (config != null && state.isPlantUnlocked(config.id)) {
                System.out.println(manager.formatPlant(config, true, state.getPlantLevel(config.id)));
            }
        }
    }

    private void addPlant(String plantName) {
        UserState state = User.currentUser.userState;
        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null || !state.isPlantUnlocked(config.id)) {
            System.out.println("Error: plant not available.");
        } else if (selectedPlants.contains(config.name)) {
            System.out.println("Error: plant already selected.");
        } else if (selectedPlants.size() >= PLANT_SLOTS) {
            System.out.println("Error: no free plant slots.");
        } else {
            selectedPlants.add(config.name);
            System.out.println(config.name + " added to loadout.");
        }
    }

    private void removePlant(String plantName) {
        if (selectedPlants.removeIf(name -> name.equalsIgnoreCase(plantName))) {
            System.out.println(plantName + " removed from loadout.");
        } else {
            System.out.println("Error: plant not in loadout.");
        }
    }

    private void boostPlant(String plantName) {
        UserState state = User.currentUser.userState;
        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null) {
            System.out.println("Error: no such plant.");
        } else if (!state.grantBoost(config.id)) {
            System.out.println("Error: plant already boosted.");
        } else {
            System.out.println(config.name + " boosted for this match.");
        }
    }

    private void startMatch() {
        Level level = currentLevel();
        if (level == null) {
            System.out.println("Error: no level loaded.");
            return;
        }
        if (level.getForcedPlants() != null) {
            for (String forced : level.getForcedPlants()) {
                if (!selectedPlants.contains(forced)) {
                    selectedPlants.add(forced);
                }
            }
        }
        GameSession.getInstance().startWaves();
        App.currentMenu = new MeanwhileMenu();
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new MatchMenu();
    }

    @Override
    public String showMenu() {
        Level level = currentLevel();
        String levelName = level != null ? level.getName() : "unknown";
        return "Stage: " + levelName + " | Loadout: " + selectedPlants + " (" + selectedPlants.size() + "/" + PLANT_SLOTS + ")";
    }
}