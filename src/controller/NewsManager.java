package controller;

import model.news.News;

public class NewsManager {

    // این متد برای دکمه منوی اصلی (Main Menu) استفاده می‌شود تا نقطه قرمز را نمایش دهد
    public static boolean hasUnreadNews() {
        // * بررسی لیست اخبار کاربر لاگین شده
        // for (News news : User.getLoggedInUser().getNewsList()) {
        //     if (!news.isRead()) return true;
        // }
        return false;
    }

    // تولید یک خبر جدید برای کاربر
    public static void generateNews(String type, String itemName) {
        String message = "";
        switch (type.toUpperCase()) {
            case "PLANT":
                message = "You unlocked a new Plant: " + itemName + "!";
                break;
            case "ZOMBIE":
                message = "New Zombie discovered in your level: " + itemName + "!";
                break;
            case "MINIGAME":
                message = "You unlocked a new Minigame: " + itemName + "!";
                break;
            case "MESSAGE":
                message = "New message from network: " + itemName;
                break;
        }

        News news = new News(message);
        // * این خبر جدید باید به لیست اخبار کاربر لاگین شده افزوده شود
        // User.getLoggedInUser().addNews(news);
        // User.getLoggedInUser().save();
    }
}