package controller.menus;

import model.App;
import model.Regex;
import model.news.News;
import java.util.regex.Matcher;
import java.util.List;

public class NewsMenu extends Menu {

    @Override
    public String getName() {
        return "News Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_NEWS_SHOW_UNREAD.getMatcherRaw(text).matches()) {
            // *
            // دریافت لیست اخبار کاربر لاگین شده از مدل اکانت/کاربر
            // List<News> userNews = App.getLoggedInUser().getNewsList();
            boolean hasUnread = false;

            // template for printing unread news:
            /*
            for (News news : userNews) {
                if (!news.isRead()) {
                    System.out.println(news.getText());
                    news.setRead(true);
                    hasUnread = true;
                }
            }
            if (!hasUnread) {
                System.out.println("No unread news.");
            }
            */
            // * ذخیره تغییرات وضعیت اخبار در پروفایل کاربر

        } else if (Regex.MENU_NEWS_SHOW_ALL.getMatcherRaw(text).matches()) {
            // *
            // دریافت کل اخبار کاربر لاگین شده
            // List<News> userNews = App.getLoggedInUser().getNewsList();

            // template for printing all news:
            /*
            if (userNews.isEmpty()) {
                System.out.println("No news available.");
            } else {
                for (News news : userNews) {
                    String status = news.isRead() ? "[READ]" : "[NEW]";
                    System.out.println(status + " " + news.getText());
                    news.setRead(true);
                }
            }
            */
            // * ذخیره تغییرات وضعیت اخبار در پروفایل کاربر

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
            matcher.matches();
            String menuName = matcher.group("menuname");
            // *
            // در صورت نیاز به هدایت کاربر به منوهای دیگر از طریق دستور enter
            // if (menuName.equals("Some Menu")) {
            //     App.currentMenu = new SomeMenu();
            // }

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return "News Menu";
    }
}