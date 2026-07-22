package controller.mini_games;

import controller.menus.Menu;
import model.Regex;
import model.match.mini_games.Beghouled;
import view.GeneralPrinter;

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
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;

        if (Regex.BEGHOULED_SWAP.getMatcherRaw(text).matches()) {
            handleSwap(text);
        } else if (Regex.BEGHOULED_UPGRADE.getMatcherRaw(text).matches()) {
            handleUpgrade(text);
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in Beghouled.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        var matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        for (int i = 0; i < ticks; i++) game.tick(0.1);
    }

    private void handleSwap(String text) {
        int[] coords = parseTwoCoords(text);
        boolean swapped = game.trySwap(coords[1], coords[0], coords[3], coords[2]);
        if (!swapped) {
            GeneralPrinter.print("That swap doesn't create a match — nothing happened.");
        }
    }

    private void handleUpgrade(String text) {
        String plantName = text.split("\\s+")[2];
        if (!game.upgrade(plantName)) {
            GeneralPrinter.print("Can't upgrade " + plantName + " (not enough sun, or no such upgrade).");
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
            GeneralPrinter.print("Reached " + game.getMatchesNeeded() + " matches — every zombie is cleared. You win!");
        } else if (game.isLost()) {
            GeneralPrinter.print("The zombies got through. You lose!");
        } else {
            GeneralPrinter.print("Matches: " + game.getMatchesMade() + "/" + game.getMatchesNeeded());
        }
    }

    @Override
    public void exitMenu() {
        changeMenu("Game Menu");
    }

    @Override
    public String showMenu() {
        return "[ Beghouled Menu ]\nCommands:\n"
                + "  swap -l (x,y) -l (x,y)\n"
                + "  upgrade -t <plant>\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
