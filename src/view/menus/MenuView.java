package view.menus;

import model.App;

import java.util.Scanner;


public abstract class MenuView {
    private static final Scanner SCANNER = new Scanner(System.in);

    public abstract void showMenu(String text);

    public boolean getInput() {
        if (!SCANNER.hasNextLine()) return false;
        String input = SCANNER.nextLine();
        if (input.equalsIgnoreCase("exit")) return false; // fore debug
        this.showMenu(input);
        App.currentMenu.handleCommand(input.trim());
        return true;
    }

    public Scanner getScanner() {
        return SCANNER;
    }
}

