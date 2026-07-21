package controller.menus;

public class NetworkMenu extends Menu{

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void handleCommand(String text){
        super.handleCommand(text);
        if (isGeneralCmd) return;






    }

    @Override
    public void exitMenu() {

    }

    @Override
    public String showMenu() {
        return "";
    }


}
