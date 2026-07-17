package controller.mini_games;

import controller.menus.Menu;
import model.match.mini_games.izombie.IZombie;

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
    public void handleCommand(String text) {
        String trimmed = text.trim();

        if (trimmed.matches("place zombie -t \\S+ -r \\d+")) {
            handlePlace(trimmed);
        } else if (trimmed.equals("advance time -t 1 ticks")) {
            game.tick(0.1);
        } else {
            System.out.println("Unknown command in I, Zombie.");
        }
        reportOutcome();
    }

    private void handlePlace(String text) {
        String[] tokens = text.split("\\s+");
        String alias = tokens[3];
        int row = Integer.parseInt(tokens[5]);

        if (!game.placeZombie(alias, row)) {
            System.out.println("Can't place " + alias + " right now (not enough sun, or unavailable this level).");
        }
    }

    private void reportOutcome() {
        if (game.isWon()) {
            System.out.println("Every brain has been eaten. You win!");
        } else if (game.isLost()) {
            System.out.println("Out of sun and out of zombies. You lose!");
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
