package controller.mini_games;

import controller.menus.Menu;
import controller.menus.TravelLogMenu;
import model.App;
import model.Regex;
import model.match.mini_games.Zombotany;
import view.GeneralPrinter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZombotanyController extends Menu {
    private static final Pattern COIN_COLLECTION = Pattern.compile(
            "^\\s*collect\\s+coin\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SUN_CHEAT = Pattern.compile(
            "^\\s*cheat\\s+(?:sun\\s+|add\\s+-n\\s+)(?<count>\\d+)\\s*(?:suns?)?\\s*$",
            Pattern.CASE_INSENSITIVE);
    private final Zombotany game;

    public ZombotanyController(Zombotany game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "Zombotany Menu";
    }

    @Override
    public void handleCommand(String text) {
        super.handleCommand(text);
        if (isGeneralCmd) return;

        if (Regex.PLANT_AT.getMatcherRaw(text).matches()
                || Regex.PLANT_ON_FIELD.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.PLANT_AT.getMatcherRaw(text).matches()
                    ? Regex.PLANT_AT.getMatcherRaw(text)
                    : Regex.PLANT_ON_FIELD.getMatcherRaw(text);
            matcher.matches();
            plant(matcher.group("type"), Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y")));
        } else if (Regex.COLLECT_ITEM.getMatcherRaw(text).matches()
                || COIN_COLLECTION.matcher(text).matches()) {
            handleCollect(text);
        } else if (Regex.COLLECT_SUN.getMatcherRaw(text).matches()) {
            handleCollect(text);
        } else if (Regex.CHEAT_ADD_SUNS.getMatcherRaw(text).matches()
                || SUN_CHEAT.matcher(text).matches()) {
            handleSunCheat(text);
        } else if (Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text).matches()) {
            advanceTime(text);
        } else if (Regex.SHOW_MAP.getMatcherRaw(text).matches()
                || text.trim().equalsIgnoreCase("show state")
                || text.trim().equalsIgnoreCase("show status")) {
            GeneralPrinter.print(game.renderState());
        } else if (text.trim().equalsIgnoreCase("show plants")
                || text.trim().equalsIgnoreCase("plants info")) {
            GeneralPrinter.print(game.renderPlantsInfo());
        } else if (Regex.SHOW_SUN_AMOUNT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print("Sun: " + game.getSession().getSunCount());
        } else if (Regex.ZOMBIES_INFO.getMatcherRaw(text).matches()
                || text.trim().equalsIgnoreCase("show zombies")) {
            GeneralPrinter.print(game.renderZombiesInfo());
        } else if (Regex.SHOW_TILE_STATUS.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.SHOW_TILE_STATUS.getMatcherRaw(text);
            matcher.matches();
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            GeneralPrinter.print(game.getSession().renderTileStatus(y - 1, x - 1));
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
            return;
        } else {
            GeneralPrinter.print("Unknown command in Zombotany.");
        }
        reportOutcome();
    }

    private void advanceTime(String text) {
        Matcher matcher = Regex.MINIGAME_ADVANCE_TIME.getMatcherRaw(text);
        matcher.matches();
        int ticks = Math.min(6000, Integer.parseInt(matcher.group("ticks")));
        int elapsed = 0;
        for (; elapsed < ticks && !game.isWon() && !game.isLost(); elapsed++) {
            game.tick(0.1);
        }
        GeneralPrinter.print("Time passes... (" + elapsed + " ticks).");
    }

    private void plant(String plantName, int x, int y) {
        if (!game.plantAt(plantName, y - 1, x - 1)) {
            GeneralPrinter.print("Plant unavailable, recharging, too expensive, "
                    + "or the tile is blocked.");
        }
    }

    private void handleCollect(String text) {
        Matcher matcher;
        if (Regex.COLLECT_ITEM.getMatcherRaw(text).matches()) {
            matcher = Regex.COLLECT_ITEM.getMatcherRaw(text);
        } else if (Regex.COLLECT_SUN.getMatcherRaw(text).matches()) {
            matcher = Regex.COLLECT_SUN.getMatcherRaw(text);
        } else {
            matcher = COIN_COLLECTION.matcher(text);
        }
        matcher.matches();
        int x = Integer.parseInt(matcher.group("x"));
        int y = Integer.parseInt(matcher.group("y"));
        List<?> collected = game.collectItemsAt(x - 1, y - 1);
        GeneralPrinter.print(collected.isEmpty()
                ? "Nothing to collect there."
                : "Collected " + collected.size() + " item(s), including coins or sun.");
    }

    private void handleSunCheat(String text) {
        Matcher matcher = Regex.CHEAT_ADD_SUNS.getMatcherRaw(text).matches()
                ? Regex.CHEAT_ADD_SUNS.getMatcherRaw(text)
                : SUN_CHEAT.matcher(text);
        matcher.matches();
        game.addSunCheat(Integer.parseInt(matcher.group("count")));
    }

    private void reportOutcome() {
        if (game.isWon()) {
            GeneralPrinter.print("All Zombotany waves cleared. You win!");
            App.currentMenu = new MiniGameEndMenu("Zombotany", true,
                    "Every Zombotany wave was cleared.");
        } else if (game.isLost()) {
            GeneralPrinter.print("The zombies got through. You lose!");
            App.currentMenu = new MiniGameEndMenu("Zombotany", false,
                    "A zombie reached the house.");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new TravelLogMenu();
    }

    @Override
    public String showMenu() {
        return "[ Zombotany Menu ]\n" + game.getStageDetails()
                + " | Available plants: " + game.getAvailablePlants()
                + " | Zombie pool: " + game.getZombiePool() + "\nCommands:\n"
                + "  plant plant -t <type> -l (<x>, <y>)\n"
                + "  show map | show state | show status | show sun amount\n"
                + "  show plants | show zombies | zombies info | show tile status -l (x,y)\n"
                + "  collect (x,y) | collect sun -l (x,y) | collect coin -l (x,y)\n"
                + "  cheat add -n <count> suns | cheat sun <count>\n"
                + "  advance time -t <n> ticks\n"
                + "  menu exit | menu show current";
    }
}
