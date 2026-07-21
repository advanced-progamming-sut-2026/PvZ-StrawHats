package controller.mini_games;

import controller.menus.Menu;
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
        String trimmed = text.trim();

        if (trimmed.matches("place zombie -t \\S+ -r \\d+")) {
            handlePlace(trimmed);
        } else if (trimmed.equals("advance time -t 1 ticks")) {
            game.tick(0.1);
        } else {
            GeneralPrinter.print("Unknown command in I, Zombie.");
        }
        reportOutcome();
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
        return "I, Zombie: place zombie -t <alias> -r <row> | advance time -t <n> ticks";
    }
}
