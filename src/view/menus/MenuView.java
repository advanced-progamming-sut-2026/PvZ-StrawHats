package view.menus;

import model.App;

import java.util.Scanner;


public abstract class MenuView {
    private Scanner scanner = new Scanner(System.in);

    public abstract void showMenu(String text);

    public void getInput() {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) return; // fore debug
        this.showMenu(input);
        App.currentMenu.handleCommand(input.trim());
    }

    public Scanner getScanner() {
        return scanner;
    }
}

