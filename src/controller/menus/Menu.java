package controller.menus;

public interface Menu {
    public void changeMenu(String text);
    public String getName();
    public void handleCommand(String text);
    public void exitMenu();
    public String showMenu();
}
