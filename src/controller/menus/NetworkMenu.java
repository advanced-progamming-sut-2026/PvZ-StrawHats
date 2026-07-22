package controller.menus;

import model.App;
import model.Regex;
import view.GeneralPrinter;

public class NetworkMenu extends Menu{

    @Override
    public String getName() {
        return "Network Menu";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;
        if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else {
            GeneralPrinter.print("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new MainMenu();
    }

    @Override
    public String showMenu() {
        return "[ Network Menu ]\nNetwork features are planned for a later phase.\n"
                + "Commands:\n  menu exit | menu show current";
    }


}
