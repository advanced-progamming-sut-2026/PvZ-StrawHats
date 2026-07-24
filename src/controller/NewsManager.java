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
        generateNews(type, itemName, null);
    }

    public static void generateNews(String type, String itemName, String info) {
        if (App.currentUser == null) return;

        String detail = (info == null || info.isBlank()) ? "" : " (" + info + ")";

        String message = switch (type.toUpperCase()) {
            case "PLANT" -> "You unlocked a new Plant: " + itemName + "!" + detail;
            case "ZOMBIE" -> "New Zombie discovered in your level: " + itemName + "!" + detail;
            case "LEVEL" -> "New level unlocked: " + itemName + "!" + detail;
            case "MINIGAME" -> "You unlocked a new Minigame: " + itemName + "!" + detail;
            case "MESSAGE" -> "New message from network: " + itemName;
            default -> itemName;
        };

        App.currentUser.userState.addNews(new News(message));
        User.save();
    }
}
