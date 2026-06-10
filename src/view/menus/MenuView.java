package view.menus;

import static model.utils.App.scanner;

public interface MenuView {
    void showMenu();
    default public String getInput(){
        if (scanner.hasNextLine())
            return scanner.nextLine().trim();
        else
            return null;
    }
}
