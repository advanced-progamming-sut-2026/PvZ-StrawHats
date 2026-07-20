package controller.mini_games;

import controller.menus.Menu;
import model.match.mini_games.wallnutbowlling.WallnutBowling;
import view.GeneralPrinter;

public class WallnutBowlingController extends Menu {
    private final WallnutBowling game;

    public WallnutBowlingController(WallnutBowling game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "Wallnut Bowling Menu";
    }

    @Override
    public void handleCommand(String text) {
        String trimmed = text.trim();

        if (trimmed.matches("plant nut -l \\(\\d+,\\s*\\d+\\)")) {
            handlePlant(trimmed);
        } else if (trimmed.equals("advance time -t 1 ticks")) {
            game.tick(0.1);
        } else {
            GeneralPrinter.print("Unknown command in Wallnut Bowling.");
        }
        reportOutcome();
    }

    private void handlePlant(String text) {
        String inner = text.substring(text.indexOf('(') + 1, text.indexOf(')'));
        String[] parts = inner.split(",");
        int col = Integer.parseInt(parts[0].trim());
        int row = Integer.parseInt(parts[1].trim());

        if (!game.plantNut(row, col)) {
            GeneralPrinter.print("Can't plant past the red line (column " + game.getRedLineColumn() + ").");
        }
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
        changeMenu("Game Menu");
    }

    @Override
    public String showMenu() {
        return "Wallnut Bowling: plant nut -l (x,y) | advance time -t <n> ticks";
    }
}
