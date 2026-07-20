package controller.menus.store;

import controller.menus.Menu;
import controller.menus.greenhouse.GreenhouseController;
import model.App;
import model.Regex;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.greenhouse.store.Store;
import model.user_data.User;
import model.user_data.UserState;
import view.GeneralPrinter;

import java.util.Map;
import java.util.regex.Matcher;

public class StoreController extends Menu {

    private static final Store STORE = new Store();

    @Override
    public String getName() {
        return "Store Menu";
    }

    @Override
    public void handleCommand(String text) {
        UserState state = User.currentUser.userState;

        if (Regex.SHOPPING_LIST.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(STORE.renderPermanentGoods());
        } else if (Regex.SHOP_DAILY.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(STORE.renderDailyOffer(state));
        } else if (Regex.SHOP_BUY.getMatcherRaw(text).matches()) {
            Matcher m = Regex.SHOP_BUY.getMatcherRaw(text);
            m.matches();
            String itemId = m.group("itemid");
            int count = Integer.parseInt(m.group("count"));
            String plantTypeName = m.group("planttype");
            Integer plantTypeId = plantTypeName == null ? null : resolvePlantId(plantTypeName);
            if (plantTypeName != null && plantTypeId == null) {
                GeneralPrinter.print("Error: unknown plant type '" + plantTypeName + "'.");
            } else {
                GeneralPrinter.print(STORE.buy(state, itemId, count, plantTypeId));
            }
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            GeneralPrinter.print(showMenu());
        } else {
            GeneralPrinter.print("Not Valid");
        }
    }

    private Integer resolvePlantId(String name) {
        String normalized = name.replace("-", "").replace("_", "").replace(" ", "").toLowerCase();
        for (Map.Entry<Integer, PlantJsonParser.PlantConfig> entry : PlantFactory.getBlueprints().entrySet()) {
            String candidate = entry.getValue().name.replace(" ", "").toLowerCase();
            if (candidate.equals(normalized)) return entry.getKey();
        }
        return null;
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GreenhouseController();
    }

    @Override
    public String showMenu() {
        return STORE.renderPermanentGoods();
    }
}
