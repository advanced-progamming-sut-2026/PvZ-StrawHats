package controller;

import model.App;
import model.news.News;
import model.user_data.User;

public class NewsManager {

    public static boolean hasUnreadNews() {
        if (App.currentUser == null) return false;
        return App.currentUser.userState.hasUnreadNews();
    }

    public static void generateNews(String type, String itemName) {
        if (App.currentUser == null) return;

        String message = switch (type.toUpperCase()) {
            case "PLANT" -> "You unlocked a new Plant: " + itemName + "!";
            case "ZOMBIE" -> "New Zombie discovered in your level: " + itemName + "!";
            case "MINIGAME" -> "You unlocked a new Minigame: " + itemName + "!";
            case "MESSAGE" -> "New message from network: " + itemName;
            default -> itemName;
        };

        App.currentUser.userState.addNews(new News(message));
        User.save();
    }
}
