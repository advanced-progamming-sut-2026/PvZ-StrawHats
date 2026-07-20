package controller.mini_games;

import controller.menus.Menu;
import model.match.mini_games.vasebreaker.Vasebreaker;
import view.GeneralPrinter;

public class VasebreakerController extends Menu {
    private final Vasebreaker game;

    public VasebreakerController(Vasebreaker game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "Vasebreaker Menu";
    }

    @Override
    public void handleCommand(String text) {
        String trimmed = text.trim();

        if (trimmed.matches("break vase -l \\(\\d+,\\s*\\d+\\)")) {
            handleBreak(trimmed);
        } else if (trimmed.matches("collect seed -l \\(\\d+,\\s*\\d+\\)")) {
            handleCollect(trimmed);
        } else if (trimmed.equals("advance time -t 1 ticks")) {
            game.tick(0.1);
        } else {
            GeneralPrinter.print("Unknown command in Vasebreaker.");
        }
        reportOutcome();
    }

    private void handleBreak(String text) {
        int[] coords = parseCoords(text);
        if (!game.breakVaseAt(coords[1], coords[0])) {
            GeneralPrinter.print("There's no vase to break at (" + coords[0] + ", " + coords[1] + ").");
        }
    }

    private void handleCollect(String text) {
        int[] coords = parseCoords(text);
        if (!game.collectSeedPacket(coords[1], coords[0])) {
            GeneralPrinter.print("No seed packet to collect at (" + coords[0] + ", " + coords[1] + ").");
        }
    }

    private int[] parseCoords(String text) {
        String inner = text.substring(text.indexOf('(') + 1, text.indexOf(')'));
        String[] parts = inner.split(",");
        return new int[] { Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()) };
    }

    private void reportOutcome() {
        if (game.isWon()) {
            GeneralPrinter.print("All vases broken. You win!");
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
        return "Vasebreaker: break vase -l (x,y) | collect seed -l (x,y) | advance time -t <n> ticks";
    }
}
