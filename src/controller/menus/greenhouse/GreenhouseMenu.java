package controller.menus.greenhouse;

import controller.menus.GameMenu;
import controller.menus.Menu;
import controller.menus.store.StoreMenu;
import model.App;
import model.Regex;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantFoodType;
import model.collections.plant.PlantJsonParser;
import model.game_exceptions.GameException;
import model.greenhouse.*;
import model.user_data.User;
import model.user_data.UserState;
import view.GeneralPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;

public class GreenhouseMenu extends Menu {

    public void plantPotPlant(Pot pot) {
        if (pot == null) {
            throw new GameException("no such pot.");
        } else if (pot.isLocked()) {
            throw new GameException("that pot is locked.");
        } else if (pot.getPotPlant() != null) {
            throw new GameException("that pot is already occupied.");
        } else {
            PotPlant potPlant;
            Random random = new Random();
            if (random.nextInt(100) < 50) {
                potPlant = new Marigold(pot);
            } else {
                UserState state = User.currentUser.userState;
                List<Integer> candidates = new ArrayList<>();
                for (Map.Entry<Integer, PlantJsonParser.PlantConfig> entry : PlantFactory.getBlueprints().entrySet()) {
                    if (state.isPlantUnlocked(entry.getKey()) && entry.getValue().plantFoodType != PlantFoodType.NONE) {
                        candidates.add(entry.getKey());
                    }
                }
                if (candidates.isEmpty()) {
                    potPlant = new Marigold(pot);
                } else {
                    int plantId = candidates.get(random.nextInt(candidates.size()));
                    potPlant = new GreenhousePlant(pot, plantId, PlantFactory.getBlueprints().get(plantId).name);
                }
            }
            pot.setPotPlant(potPlant);
            GeneralPrinter.print("Planted " + potPlant.getPlantName() + " at (" + pot.getCol() + "," + pot.getRow() + ").");
            User.save();
        }
    }

    @Override
    public String getName() {
        return "Greenhouse Menu";
    }

    @Override
    public void handleCommand(String text) {
        try {
            if (Regex.ENTER_SHOP.getMatcherRaw(text).matches()) {
                App.currentMenu = new StoreMenu();
                return;
            }
            super.handleCommand(text);
            if (isGeneralCmd) return;
            handleGreenhouseCommand(text);
        } catch (Exception e) {
            throw new GameException("could not process that command (" + e.getMessage() + ").");
        }
    }

    private void handleGreenhouseCommand(String text) {
        UserState state = User.currentUser.userState;

        if (Regex.SHOW_GREENHOUSE.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(Greenhouse.getInstance().renderStatus());
        } else if (Regex.PLANT_POT_AT.getMatcherRaw(text).matches()) {
            Matcher m = Regex.PLANT_POT_AT.getMatcherRaw(text);
            m.matches();
            int x = Integer.parseInt(m.group("x"));
            int y = Integer.parseInt(m.group("y"));
            plantPotPlant(Greenhouse.getInstance().getPot(x, y));
        } else if (Regex.COLLECT_POT.getMatcherRaw(text).matches()) {
            Matcher m = Regex.COLLECT_POT.getMatcherRaw(text);
            m.matches();
            int x = Integer.parseInt(m.group("x"));
            int y = Integer.parseInt(m.group("y"));
            Pot pot = Greenhouse.getInstance().getPot(x, y);
            if (pot == null) {
                throw new GameException("no such pot.");
            } else {
                GeneralPrinter.print(pot.getPotController().collect(state));
            }
        } else if (Regex.GROW_POT.getMatcherRaw(text).matches()) {
            Matcher m = Regex.GROW_POT.getMatcherRaw(text);
            m.matches();
            int x = Integer.parseInt(m.group("x"));
            int y = Integer.parseInt(m.group("y"));
            Pot pot = Greenhouse.getInstance().getPot(x, y);
            if (pot == null) {
                throw new GameException("no such pot.");
            } else {
                GeneralPrinter.print(pot.getPotController().grow(state));
            }
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else {
            GeneralPrinter.print("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "[ Greenhouse Menu ]\n"
                + Greenhouse.getInstance().renderStatus() + "\n"
                + "Commands:\n"
                + "  show greenhouse\n"
                + "  plant pot at (<x>, <y>)\n"
                + "  collect (<x>, <y>)\n"
                + "  grow (<x>, <y>)\n"
                + "  enter shop\n"
                + "  menu exit | menu show current";
    }



}
