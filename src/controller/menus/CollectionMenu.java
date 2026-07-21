package controller.menus;


import controller.CollectionManager;
import model.App;
import model.Regex;
import model.collections.plant.PlantJsonParser;
import model.collections.zombie.Zombie;
import model.user_data.User;
import model.user_data.UserState;

import java.util.regex.Matcher;

public class CollectionMenu extends Menu {
    private final CollectionManager manager = new CollectionManager();

    @Override
    public String getName() {
        return "Collection Menu";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;





        UserState state = User.currentUser.userState;

        if (Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches()) {
            showAllPlants(state);
        } else if (Regex.MENU_COLLECTION_SHOW_PLANTS.getMatcherRaw(text).matches()) {
            showUnlockedPlants(state);
        } else if (Regex.MENU_COLLECTION_SHOW_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_COLLECTION_SHOW_PLANT.getMatcherRaw(text);
            matcher.matches();
            showOnePlant(state, matcher.group("plantname"));
        } else if (Regex.MENU_COLLECTION_UPGRADE_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_COLLECTION_UPGRADE_PLANT.getMatcherRaw(text);
            matcher.matches();
            upgradePlant(state, matcher.group("plantname"));
        } else if (Regex.MENU_COLLECTION_SHOW_ALL_ZOMBIES.getMatcherRaw(text).matches()) {
            showAllZombies();
        } else if (Regex.MENU_COLLECTION_SHOW_ZOMBIES.getMatcherRaw(text).matches()) {
            showSeenZombies(state);
        } else if (Regex.MENU_COLLECTION_SHOW_ZOMBIE.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_COLLECTION_SHOW_ZOMBIE.getMatcherRaw(text);
            matcher.matches();
            showOneZombie(state, matcher.group("zombiename"));
        } else if (Regex.MENU_COLLECTION_PURCHASE_PLANT.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_COLLECTION_PURCHASE_PLANT.getMatcherRaw(text);
            matcher.matches();
            purchasePlant(state, matcher.group("plantname"));
        }  else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else {
            System.out.println("Not Valid");
        }
    }

    private void showAllPlants(UserState state) {
        for (PlantJsonParser.PlantConfig config : manager.getAllPlants()) {
            boolean unlocked = state.isPlantUnlocked(config.id);
            System.out.println(manager.formatPlant(config, unlocked, state.getPlantLevel(config.id)));
        }
    }

    private void showUnlockedPlants(UserState state) {
        for (PlantJsonParser.PlantConfig config : manager.getUnlockedPlants(state)) {
            System.out.println(manager.formatPlant(config, true, state.getPlantLevel(config.id)));
        }
    }

    private void showOnePlant(UserState state, String plantName) {
        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null) {
            System.out.println("Error: no such plant.");
            return;
        }
        boolean unlocked = state.isPlantUnlocked(config.id);
        System.out.println(manager.formatPlant(config, unlocked, state.getPlantLevel(config.id)));
    }

    private void showAllZombies() {
        for (String alias : manager.getAllZombieAliases()) {
            System.out.println(manager.formatZombie(manager.findZombie(alias)));
        }
    }

    private void showSeenZombies(UserState state) {
        for (String alias : manager.getSeenZombieAliases(state)) {
            System.out.println(manager.formatZombie(manager.findZombie(alias)));
        }
    }

    private void showOneZombie(UserState state, String zombieName) {
        if (!manager.getSeenZombieAliases(state).contains(zombieName)) {
            System.out.println("Error: zombie not found or not yet observed.");
            return;
        }
        Zombie zombie = manager.findZombie(zombieName);
        System.out.println(manager.formatZombie(zombie));
    }

    private void purchasePlant(UserState state, String plantName) {
        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null) {
            System.out.println("Error: no such plant.");
        } else if (state.isPlantUnlocked(config.id)) {
            System.out.println("Error: plant already unlocked.");
        } else if (!manager.purchasePlant(state, config)) {
            System.out.println("Error: not enough coins.");
        } else {
            System.out.println("Plant purchased: " + config.name);
        }
    }

    private void upgradePlant(UserState state, String plantName) {
        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null) {
            System.out.println("Error: no such plant.");
        } else if (!state.isPlantUnlocked(config.id)) {
            System.out.println("Error: plant is locked.");
        } else if (!manager.upgradePlant(state, config)) {
            System.out.println("Error: not enough coins or seed packets.");
        } else {
            System.out.println("Plant upgraded: " + config.name + " -> level " + state.getPlantLevel(config.id));
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    
}
