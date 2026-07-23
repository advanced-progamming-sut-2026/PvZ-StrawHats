package controller.mini_games;

import controller.menus.Menu;
import controller.menus.TravelLogMenu;
import model.App;
import model.Regex;
import model.game_exceptions.GameException;
import model.match.mini_games.Zombotany;
import view.GeneralPrinter;

public class ZombotanyController extends Menu {
    private final Zombotany game;

    public ZombotanyController(Zombotany game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "Zombotany Menu";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;


        if (Regex.PLANT_AT.getMatcherRaw(text).matches()
                || Regex.PLANT_ON_FIELD.getMatcherRaw(text).matches()) {
            var matcher = Regex.PLANT_AT.getMatcherRaw(text).matches()
                    ? Regex.PLANT_AT.getMatcherRaw(text) : Regex.PLANT_ON_FIELD.getMatcherRaw(text);
            matcher.matches();
            plant(matcher.group("type"), Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")));
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.SHOW_MAP.getMatcherRaw(text).matches()
                || text.trim().equalsIgnoreCase("show status")) {
            GeneralPrinter.print(game.renderState());
        } else if (Regex.SHOW_SUN_AMOUNT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print("Sun: " + game.getSession().getSunCount());
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in Zombotany.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        var matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        for (int i = 0; i < ticks; i++) game.tick(0.1);
    }

    private void plant(String plantName, int x, int y) {
        if (!game.plantAt(plantName, y - 1, x - 1)) {
            throw new GameException("plant unavailable, recharging, too expensive, or the tile is blocked.");
        }
        GeneralPrinter.print(plantName + " planted at (" + x + ", " + y + "). Sun: "
                + game.getSession().getSunCount() + ".");
    }

    private void reportOutcome() {
        if (game.isWon()) {
            GeneralPrinter.print("All waves cleared. You win!");
        } else if (game.isLost()) {
            GeneralPrinter.print("The zombies got through. You lose!");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new TravelLogMenu();
    }

    @Override
    public String showMenu() {
        return "[ Zombotany Menu ]\n" + game.getStageDetails()
                + " | Available plants: " + game.getAvailablePlants()
                + " | Zombie pool: " + game.getZombiePool() + "\nCommands:\n"
                + "  plant plant -t <type> -l (<x>, <y>)\n"
                + "  show map | show status | show sun amount\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
