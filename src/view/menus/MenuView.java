package view.menus;

import java.util.Scanner;


public abstract class MenuView {
    public abstract void showMenu(String text);
    public  void getInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break; // baraye debug
            this.showMenu(input);
        }
    }
}
