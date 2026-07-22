package controller.mini_games;

import controller.menus.Menu;
import model.Regex;
import model.match.mini_games.izombie.IZombie;
import view.GeneralPrinter;

public class ImZombieController extends Menu {
    private final IZombie game;

    public ImZombieController(IZombie game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "I, Zombie Menu";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;

        if (Regex.IZOMBIE_PLACE_ZOMBIE.getMatcherRaw(text).matches()) {
            handlePlace(text);
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in I, Zombie.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        var matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        for (int i = 0; i < ticks; i++) game.tick(0.1);
    }

    private void handlePlace(String text) {
        String[] tokens = text.split("\\s+");
        String alias = tokens[3];
        int row = Integer.parseInt(tokens[5]);

        if (!game.placeZombie(alias, row)) {
            GeneralPrinter.print("Can't place " + alias + " right now (not enough sun, or unavailable this level).");
        }
    }

    private void reportOutcome() {
        if (game.isWon()) {
            GeneralPrinter.print("Every brain has been eaten. You win!");
        } else if (game.isLost()) {
            GeneralPrinter.print("Out of sun and out of zombies. You lose!");
        }
    }

    @Override
    public void exitMenu() {
        changeMenu("Game Menu");
    }

    @Override
    public String showMenu() {
        return "[ I, Zombie Menu ]\nCommands:\n"
                + "  place zombie -t <alias> -r <row>\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
