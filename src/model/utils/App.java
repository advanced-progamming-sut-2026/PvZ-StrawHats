package model.utils;

import controller.menus.Menu;
import controller.menus.authentication.SignupMenu;
import model.match.main.MainMode;
import model.user_data.User;

public class App {
    public static User currentUser = null;

    public static Menu currentMenu = new SignupMenu();

    public static MainMode currentMatch = null;
}
