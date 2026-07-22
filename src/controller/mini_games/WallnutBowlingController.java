package controller.mini_games;

import controller.menus.Menu;
import model.Regex;
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
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;


        if (Regex.WALLNUT_PLANT_NUT.getMatcherRaw(text).matches()) {
            handlePlant(text);
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in Wallnut Bowling.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        var matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        for (int i = 0; i < ticks; i++) game.tick(0.1);
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
        return "[ Wallnut Bowling Menu ]\nCommands:\n"
                + "  plant nut -l (x,y)\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
