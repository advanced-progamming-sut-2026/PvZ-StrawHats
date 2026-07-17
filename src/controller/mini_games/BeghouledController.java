package controller.mini_games;

import controller.menus.Menu;
import model.match.mini_games.Beghouled;

public class BeghouledController extends Menu {
    private final Beghouled game;

    public BeghouledController(Beghouled game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "Beghouled Menu";
    }

    @Override
    public void handleCommand(String text) {
        String trimmed = text.trim();

        if (trimmed.matches("swap -l \\(\\d+,\\s*\\d+\\) -l \\(\\d+,\\s*\\d+\\)")) {
            handleSwap(trimmed);
        } else if (trimmed.matches("upgrade -t \\S+")) {
            handleUpgrade(trimmed);
        } else if (trimmed.equals("advance time -t 1 ticks")) {
            game.tick(0.1);
        } else {
            System.out.println("Unknown command in Beghouled.");
        }
        reportOutcome();
    }

    private void handleSwap(String text) {
        int[] coords = parseTwoCoords(text);
        boolean swapped = game.trySwap(coords[1], coords[0], coords[3], coords[2]);
        if (!swapped) {
            System.out.println("That swap doesn't create a match — nothing happened.");
        }
    }

    private void handleUpgrade(String text) {
        String plantName = text.split("\\s+")[2];
        if (!game.upgrade(plantName)) {
            System.out.println("Can't upgrade " + plantName + " (not enough sun, or no such upgrade).");
        }
    }

    private int[] parseTwoCoords(String text) {
        String[] tokens = text.replace("(", "").replace(")", "").replace(",", " ").split("\\s+");
        // tokens: swap -l x1 y1 -l x2 y2
        return new int[] {
                Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
                Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6])
        };
    }

    private void reportOutcome() {
        if (game.isWon()) {
            System.out.println("Reached " + game.getMatchesNeeded() + " matches — every zombie is cleared. You win!");
        } else if (game.isLost()) {
            System.out.println("The zombies got through. You lose!");
        } else {
            System.out.println("Matches: " + game.getMatchesMade() + "/" + game.getMatchesNeeded());
        }
    }

    @Override
    public void exitMenu() {
        changeMenu("Game Menu");
    }

    @Override
    public String showMenu() {
        return "Beghouled: swap -l (x,y) -l (x,y) | upgrade -t <plant> | advance time -t <n> ticks";
    }
}
