package view;

import model.App;
import view.menus.*;
import view.menus.collection_view.CollectionMenuView;
import view.menus.while_match.BeforeMatchView;
import view.menus.while_match.MatchMenuView;

import java.util.Scanner;

public class AppView {
    public static void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            MenuView currentView = resolveView(App.currentMenu.getName());
            if (currentView == null) {
                System.out.println("Unknown menu state, exiting.");
                break;
            }

            String input = scanner.nextLine();
            currentView.showMenu(input);
        }
    }

    private static MenuView resolveView(String menuName) {
        return switch (menuName) {
            case "SignUP Menu" -> new SignupMenuView();
            case "Login Menu" -> new LoginMenuView();
            case "Main Menu" -> new MainMenuView();
            case "Profile Menu" -> new ProfileMenuView();
            case "Game Menu" -> new GameMenuView();
            case "News Menu" -> new NewsMenuView();
            case "Collection Menu" -> new CollectionMenuView();
            case "Before Menu" -> new BeforeMatchView();
            case "Setting Menu" -> new SettingMenuView();
            case "Greenhouse Menu" -> new GreenhouseMenuView();
            case "Store Menu" -> new StoreMenuView();
            case "Travel Log Menu" -> new TravelLogMenuView();
            case "Match Menu" -> new MatchMenuView();
            case "Leaderboard Menu" -> new LeaderboardMenuView();
            default -> null;
        };
    }
}
