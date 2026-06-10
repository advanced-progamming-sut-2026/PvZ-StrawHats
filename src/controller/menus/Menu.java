package controller.menus;

public interface Menu {
    public void changeMenu(Menu targetMenu);
    public String getName();
    public void handleCommand();
}
