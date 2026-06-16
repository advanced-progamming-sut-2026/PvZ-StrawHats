package model.utils;

import controller.menus.Menu;
import controller.menus.authentication.SignupMenu;
import model.match.main.MainMode;

import java.util.Scanner;

public class App {
    public static Menu currentMenu = new SignupMenu();

    public static MainMode currentMatch = null;
}
