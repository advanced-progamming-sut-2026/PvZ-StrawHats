package controller.menus.match;

import controller.CollectionManager;
import controller.menus.GameMenu;
import controller.menus.Menu;
import model.App;
import model.Regex;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.item.GroundItem;
import model.match_mechanisms.vector.Position;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;
import view.GeneralPrinter;

import java.util.List;
import java.util.regex.Matcher;

public class MeanwhileMenu extends Menu {

    private final CollectionManager manager = new CollectionManager();

    @Override
    public String getName() {
        return "Meanwhile Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (text.equals("pause")) {
            GeneralPrinter.print(showMenu());
        } else if (text.equals("resume")) {
            GeneralPrinter.print("Resuming match...");
        } else if (text.equals("restart")) {
            restartMatch();
        } else if (text.startsWith("end game")) {
            finishMatch(text.replace("end game", "").replace("-r", "").trim());
        } else if (Regex.PLANT_AT.getMatcherRaw(text).matches()) {
            Matcher m = Regex.PLANT_AT.getMatcherRaw(text);
            m.matches();
            plantAt(m.group("type"), Integer.parseInt(m.group("x")), Integer.parseInt(m.group("y")));
        } else if (Regex.REMOVE_PLANT_AT.getMatcherRaw(text).matches()) {
            Matcher m = Regex.REMOVE_PLANT_AT.getMatcherRaw(text);
            m.matches();
            removePlantAt(Integer.parseInt(m.group("x")), Integer.parseInt(m.group("y")));
        } else if (Regex.COLLECT_ITEM.getMatcherRaw(text).matches()) {
            Matcher m = Regex.COLLECT_ITEM.getMatcherRaw(text);
            m.matches();
            collectAt(Integer.parseInt(m.group("x")), Integer.parseInt(m.group("y")));
        } else if (Regex.USE_PLANT_FOOD.getMatcherRaw(text).matches()) {
            Matcher m = Regex.USE_PLANT_FOOD.getMatcherRaw(text);
            m.matches();
            useFoodAt(Integer.parseInt(m.group("x")), Integer.parseInt(m.group("y")));
        } else if (Regex.SHOW_GARDEN.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(GameSession.getInstance().renderMap());
            GeneralPrinter.print(GameSession.getInstance().renderPlantsStatus());
        } else if (Regex.SHOW_TILE.getMatcherRaw(text).matches()) {
            Matcher m = Regex.SHOW_TILE.getMatcherRaw(text);
            m.matches();
            int x = Integer.parseInt(m.group("x"));
            int y = Integer.parseInt(m.group("y"));
            GeneralPrinter.print(GameSession.getInstance().renderTileStatus(y - 1, x - 1));
        } else if (Regex.WAIT_SECONDS.getMatcherRaw(text).matches()) {
            Matcher m = Regex.WAIT_SECONDS.getMatcherRaw(text);
            m.matches();
            waitSeconds(Integer.parseInt(m.group("seconds")));
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(showMenu());
        } else {
            GeneralPrinter.print("Not Valid");
        }
    }

    private void plantAt(String plantName, int x, int y) {
        GameSession session = GameSession.getInstance();
        UserState state = User.currentUser.userState;

        PlantJsonParser.PlantConfig config = manager.findPlant(plantName);
        if (config == null || !state.isPlantUnlocked(config.id)) {
            GeneralPrinter.print("Error: plant not available.");
            return;
        }
        if (!BeforeMenu.selectedPlants.contains(config.name)) {
            GeneralPrinter.print("Error: plant not in this match's loadout.");
            return;
        }

        int row = y - 1;
        int col = x - 1;
        int level = state.getPlantLevel(config.id);
        Plant plant = PlantFactory.createPlant(config.id, level, new Position(col, row));

        if (state.consumeBoost(config.id)) {
            plant.setHP((int) Math.round(plant.getHP() * 1.5));
            plant.setDamage((int) Math.round(plant.getDamage() * 1.5));
        }

        if (!session.plantAt(row, col, plant)) {
            GeneralPrinter.print("Error: that tile is occupied or out of bounds.");
        } else {
            GeneralPrinter.print(config.name + " planted at (" + x + "," + y + ").");
        }
    }

    private void removePlantAt(int x, int y) {
        boolean removed = GameSession.getInstance().removePlantAt(y - 1, x - 1);
        GeneralPrinter.print(removed ? "Plant removed." : "Error: no plant there.");
    }

    private void collectAt(int x, int y) {
        List<GroundItem> collected = GameSession.getInstance()
                .collectItemsNear(new Position(x - 1, y - 1));
        if (collected.isEmpty()) {
            GeneralPrinter.print("Nothing to collect there.");
        } else {
            GeneralPrinter.print("Collected " + collected.size() + " item(s).");
        }
    }

    private void useFoodAt(int x, int y) {
        GameSession session = GameSession.getInstance();
        Plant plant = null;
        for (Plant p : session.getPlants()) {
            if (p.isAlive() && p.getPosition() != null
                    && (int) p.getPosition().x() == x - 1
                    && (int) p.getPosition().y() == y - 1) {
                plant = p;
                break;
            }
        }
        if (plant == null) {
            GeneralPrinter.print("Error: no plant there.");
        } else if (!session.spendPlantFood()) {
            GeneralPrinter.print("Error: no plant food available.");
        } else {
            plant.activatePlant(session);
            GeneralPrinter.print("Plant food used on " + plant.getName() + ".");
        }
    }

    private void waitSeconds(int seconds) {
        GameSession session = GameSession.getInstance();
        int ticks = Math.min(seconds, 60) * 10;
        for (int i = 0; i < ticks; i++) {
            session.tick();
            if (session.isGameOver() || session.isGameWon()) break;
        }
        if (session.isGameOver()) {
            finishMatch("lose");
        } else if (session.isGameWon()) {
            finishMatch("win");
        } else {
            GeneralPrinter.print("Time passes... (" + seconds + "s)");
        }
    }

    private void restartMatch() {
        GameSession session = GameSession.getInstance();
        if (session.getLevel() == null) {
            GeneralPrinter.print("Error: no active match.");
            return;
        }
        GameSession fresh = new GameSession(session.getRows(), session.getCols());
        fresh.setLevel(session.getLevel());
        fresh.startWaves();
        GeneralPrinter.print("Match restarted.");
    }

    private void finishMatch(String result) {
        boolean won = result.equalsIgnoreCase("win");
        AfterMenu.reset(won);
        App.currentMenu = new AfterMenu();
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "Paused - Options: plant -t <name> at (x,y) | remove plant at (x,y) | collect (x,y) | use food at (x,y) | show garden | show tile (x,y) | wait <seconds> | resume | restart | menu exit";
    }
}
