package controller.mini_games;

import controller.menus.Menu;
import model.Regex;
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


        if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
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
        return "[ Zombotany Menu ]\nCommands:\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
