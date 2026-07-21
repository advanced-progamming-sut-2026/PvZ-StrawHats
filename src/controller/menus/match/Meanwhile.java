package controller.menus.match;

import controller.CollectionManager;
import controller.menus.GameMenu;
import controller.menus.Menu;
import model.App;
import model.Regex;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;
import view.GeneralPrinter;

import java.util.regex.Matcher;

public class Meanwhile extends Menu {

    private final CollectionManager manager = new CollectionManager();

    @Override
    public String getName() {
        return "Meanwhile Menu";
    }

    @Override
    public void handleCommand(String text) {
        String trimmed = text.trim().toLowerCase();

        if (trimmed.equals("pause")) {
            System.out.println(showMenu());
        } else if (trimmed.equals("resume")) {
            System.out.println("Resuming match...");
        } else if (trimmed.equals("restart")) {
            restartMatch();
        } else if (trimmed.startsWith("end game")) {
            finishMatch(trimmed.replace("end game", "").replace("-r", "").trim());
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else if (Regex.ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
            checkMatchOutcome();
        } else if (Regex.SHOW_MAP.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(GameSession.getInstance().renderMap());
        } else if (Regex.SHOW_SUN_AMOUNT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print("You have " + GameSession.getInstance().getSunCount() + " sun.");
        } else if (Regex.SHOW_PLANT_STATUS.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(GameSession.getInstance().renderPlantsStatus());
        } else if (Regex.SHOW_TILE_STATUS.getMatcherRaw(text).matches()) {
            showTileStatus(text);
        } else if (Regex.ZOMBIES_INFO.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(GameSession.getInstance().renderZombiesInfo());
        } else if (Regex.PLANT_ON_FIELD.getMatcherRaw(text).matches()) {
            plantOnField(text);
        } else if (Regex.PLUCK_PLANT_FIELD.getMatcherRaw(text).matches()) {
            pluckPlant(text);
        } else if (Regex.FEED_PLANT_FIELD.getMatcherRaw(text).matches()) {
            feedPlant(text);
        } else if (Regex.CHEAT_ADD_SUNS.getMatcherRaw(text).matches()) {
            cheatAddSuns(text);
        } else if (Regex.CHEAT_ADD_PLANT_FOOD.getMatcherRaw(text).matches()) {
            GameSession.getInstance().addPlantFood();
            GeneralPrinter.print("Cheat: added 1 plant food. You have "
                    + GameSession.getInstance().getPlantFoodCount() + " now.");
        } else if (Regex.CHEAT_REMOVE_COOLDOWN.getMatcherRaw(text).matches()) {
            GameSession.getInstance().removeAllCooldowns();
            GeneralPrinter.print("Cheat: all planting cooldowns cleared.");
        } else if (Regex.CHEAT_SPAWN_ZOMBIE.getMatcherRaw(text).matches()) {
            cheatSpawnZombie(text);
            checkMatchOutcome();
        } else if (Regex.RELEASE_THE_NUKE.getMatcherRaw(text).matches()) {
            GameSession.getInstance().killAllZombies();
            GeneralPrinter.print("release the nuke: every zombie on the lawn is gone.");
            checkMatchOutcome();
        } else {
            System.out.println("Not Valid");
        }
    }

    private void advanceTime(String text) {
        Matcher matcher = Regex.ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Integer.parseInt(matcher.group("ticks"));

        GameSession session = GameSession.getInstance();
        for (int i = 0; i < ticks && !session.isGameOver() && !session.isGameWon(); i++) {
            session.tick();
        }
    }

    private void showTileStatus(String text) {
        Matcher matcher = Regex.SHOW_TILE_STATUS.getMatcherRaw(text);
        matcher.matches();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        GeneralPrinter.print(GameSession.getInstance().renderTileStatus(y, x));
    }

    private void plantOnField(String text) {
        Matcher matcher = Regex.PLANT_ON_FIELD.getMatcherRaw(text);
        matcher.matches();
        String type = matcher.group("type").trim();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));

        PlantJsonParser.PlantConfig config = manager.findPlant(type);
        if (config == null) {
            GeneralPrinter.print("Error: no such plant type '" + type + "'.");
            return;
        }
        if (!Before.selectedPlants.isEmpty() && !Before.selectedPlants.contains(config.name)) {
            GeneralPrinter.print("Error: " + config.name + " is not in your loadout for this stage.");
            return;
        }

        GameSession session = GameSession.getInstance();
        Cell cell = session.getEnvironment().getCell(y, x);
        if (cell == null) {
            GeneralPrinter.print("Error: (" + x + ", " + y + ") is outside the lawn.");
            return;
        }
        if (cell.hasPlant()) {
            GeneralPrinter.print("Error: tile (" + x + ", " + y + ") is already occupied.");
            return;
        }
        if (!session.spendSun(config.cost)) {
            GeneralPrinter.print("Error: not enough sun (need " + config.cost + ", have " + session.getSunCount() + ").");
            return;
        }

        UserState state = User.currentUser.userState;
        int level = state.getPlantLevel(config.id);
        Plant plant = PlantFactory.createPlant(config.id, level, new Position(x, y));
        session.plantAt(y, x, plant);
        GeneralPrinter.print(config.name + " planted at (" + x + ", " + y + ").");
    }

    private void pluckPlant(String text) {
        Matcher matcher = Regex.PLUCK_PLANT_FIELD.getMatcherRaw(text);
        matcher.matches();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));

        if (GameSession.getInstance().removePlantAt(y, x)) {
            GeneralPrinter.print("Removed the plant at (" + x + ", " + y + ").");
        } else {
            GeneralPrinter.print("Error: no plant at (" + x + ", " + y + ").");
        }
    }

    private void feedPlant(String text) {
        Matcher matcher = Regex.FEED_PLANT_FIELD.getMatcherRaw(text);
        matcher.matches();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));

        GameSession session = GameSession.getInstance();
        Cell cell = session.getEnvironment().getCell(y, x);
        if (cell == null || !cell.hasPlant()) {
            GeneralPrinter.print("Error: no plant at (" + x + ", " + y + ").");
            return;
        }
        if (!session.spendPlantFood()) {
            GeneralPrinter.print("Error: you have no plant food to use.");
            return;
        }
        cell.getPlant().activatePlant(session);
        GeneralPrinter.print("Fed " + cell.getPlant().getName() + " at (" + x + ", " + y + ").");
    }

    private void cheatAddSuns(String text) {
        Matcher matcher = Regex.CHEAT_ADD_SUNS.getMatcherRaw(text);
        matcher.matches();
        int amount = Integer.parseInt(matcher.group("count"));
        GameSession.getInstance().addSun(amount);
        GeneralPrinter.print("Cheat: added " + amount + " sun. You now have "
                + GameSession.getInstance().getSunCount() + ".");
    }

    private void cheatSpawnZombie(String text) {
        Matcher matcher = Regex.CHEAT_SPAWN_ZOMBIE.getMatcherRaw(text);
        matcher.matches();
        String type = matcher.group("type");
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));

        try {
            Zombie zombie = ZombieFactory.create(type, y, x);
            GameSession.getInstance().spawnZombie(zombie);
            GeneralPrinter.print("Cheat: spawned " + type + " at (" + x + ", " + y + ").");
        } catch (IllegalArgumentException e) {
            GeneralPrinter.print("Error: unknown zombie type '" + type + "'.");
        }
    }

    private void checkMatchOutcome() {
        GameSession session = GameSession.getInstance();
        if (session.isGameOver()) {
            After.reset(false);
            App.currentMenu = new After();
        } else if (session.isGameWon()) {
            After.reset(true);
            App.currentMenu = new After();
        }
    }

    private void restartMatch() {
        GameSession session = GameSession.getInstance();
        if (session.getLevel() == null) {
            System.out.println("Error: no active match.");
            return;
        }
        GameSession fresh = new GameSession(session.getRows(), session.getCols());
        fresh.setLevel(session.getLevel());
        fresh.startWaves();
        System.out.println("Match restarted.");
    }

    private void finishMatch(String result) {
        boolean won = result.equalsIgnoreCase("win");
        After.reset(won);
        App.currentMenu = new After();
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "Paused - Options: resume | restart | menu exit | menu show current";
    }
}