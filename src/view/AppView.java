package view;

import model.App;
import view.menus.*;
import view.menus.collection_view.CollectionMenuView;
import view.menus.while_match.BeforeMatchView;
import view.menus.while_match.MatchMenuView;

public class AppView {
    public static void run() {
        switch (App.currentMenu.getName()) {
            case "SignUP Menu":
                new SignupMenuView().getInput();
                break;
            case "Login Menu":
                new LoginMenuView().getInput();
                break;
            case "Main Menu":
                new MainMenuView().getInput();
                break;
            case "Profile Menu":
                new ProfileMenuView().getInput();
                break;
            case "Game Menu":
                new GameMenuView().getInput();
                break;
            case "News Menu":
                new NewsMenuView().getInput();
                break;
            case "Collection Menu":
                new CollectionMenuView().getInput();
                break;
            case "Before Menu":
                new BeforeMatchView().getInput();
                break;
            case "Setting Menu":
                new SettingMenuView().getInput();
                break;
            case "Greenhouse Menu":
                new GreenhouseMenuView().getInput();
                break;
            case "Store Menu":
                new StoreMenuView().getInput();
                break;
            case "Travel Log Menu":
                new TravelLogMenuView().getInput();
                break;
            case "Match Menu":
                new MatchMenuView().getInput();
                break;
            case "Leaderboard Menu":
                new LeaderboardMenuView().getInput();
                break;
        }
    }
}