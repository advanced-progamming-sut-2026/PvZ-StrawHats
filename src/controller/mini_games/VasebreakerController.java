package controller.mini_games;

import controller.menus.Menu;
import model.Regex;
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
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;


        if (Regex.VASEBREAKER_BREAK_VASE.getMatcherRaw(text).matches()) {
            handleBreak(text);
        } else if (Regex.VASEBREAKER_COLLECT_SEED.getMatcherRaw(text).matches()) {
            handleCollect(text);
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in Vasebreaker.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        var matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        for (int i = 0; i < ticks; i++) game.tick(0.1);
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
        return "[ Vasebreaker Menu ]\nCommands:\n"
                + "  break vase -l (x,y)\n"
                + "  collect seed -l (x,y)\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
