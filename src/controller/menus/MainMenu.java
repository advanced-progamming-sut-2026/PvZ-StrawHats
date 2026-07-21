package controller.menus;

import controller.NewsManager;
import controller.menus.authentication.LoginMenu;
import model.App;
import model.Regex;
import model.user_data.User;
import view.GeneralPrinter;

public class MainMenu extends Menu{

    @Override
    public String getName() {
        return "Main Menu";
    }

    @Override
    public void handleCommand(String text){
    super.handleCommand(text);


        super.handleCommand(text);
        if (Regex.MENU_LOGOUT.getMatcherRaw(text).matches()) {
            Logout();

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        }  else {
            GeneralPrinter.print("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        Logout();
    }

    @Override
    public String  showMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ Main Menu ]\n");
        sb.append("  Game\n");
        sb.append("  Settings\n");
        sb.append(NewsManager.hasUnreadNews() ? "  News [!] (you have unread news)\n" : "  News\n");
        sb.append("  Profile\n");
        sb.append("Commands:\n");
        sb.append("  menu enter <menu_name>\n");
        sb.append("  menu logout\n");
        sb.append("  menu show current");
        return sb.toString();
    }

    public void Logout() {
        User.save();
        App.currentMenu = new LoginMenu();
        App.currentUser = null;
        User.currentUser.stayLoggedIn = false;
        User.currentUser = null;
        GeneralPrinter.print("Logged out successfully.");
    }

}
