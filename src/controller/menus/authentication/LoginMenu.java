package controller.menus.authentication;

import controller.menus.Menu;
import controller.menus.MainMenu;
import model.user_data.User;
import model.Regex;

import java.util.regex.Matcher;

import static model.App.currentMenu;

public class LoginMenu extends Menu {

    private User pendingPasswordReset = null;
    private boolean awaitingNewPassword = false;

    @Override
    public String getName() {
        return "Login Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (awaitingNewPassword) {
            handleNewPassword(text);
            return;
        }

        if (Regex.LOGIN.getMatcherRaw(text).matches()) {
            handleLogin(text);
        } else if (Regex.FORGET_PASSWORD.getMatcherRaw(text).matches()) {
            handleForgetPassword(text);
        } else if (Regex.ANSWER.getMatcherRaw(text).matches()) {
            handleAnswer(text);
        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            handleMenuEnter(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            System.out.println("Invalid command.");
        }
    }

    private void handleLogin(String text) {
        Matcher matcher = Regex.LOGIN.getMatcherRaw(text);
        matcher.matches();

        String username = matcher.group("username"), password = matcher.group("password");
        boolean stayLoggedIn = text.contains("-stay-logged-in");

        User user = User.findByUsername(username);
        if (user == null) {
            System.out.println("Username not found.");
            return;
        }
        if (!user.checkPassword(password)) {
            System.out.println("Incorrect password.");
            return;
        }

        for (User u : User.users) {
            u.stayLoggedIn = false;
        }

        user.stayLoggedIn = stayLoggedIn;
        User.setUser(user);

        User.save();

        System.out.println("Welcome back, " + user.nickname + "!");
        currentMenu = new MainMenu();
    }

    private void handleForgetPassword(String text) {
        Matcher matcher = Regex.FORGET_PASSWORD.getMatcherRaw(text);
        matcher.matches();

        String username = matcher.group("username"), email = matcher.group("email");

        User user = User.findByUsername(username);
        if (user == null) {
            System.out.println("Username not found.");
            return;
        }
        if (!user.email.equals(email)) {
            System.out.println("Email does not match our records.");
            return;
        }
        if (user.securityQuestion == null) {
            System.out.println("No security question set for this account.");
            return;
        }

        pendingPasswordReset = user;
        System.out.println("Security question: " + user.securityQuestion);
        System.out.println("Answer with: answer -a <your_answer>");
    }

    private void handleAnswer(String text) {
        if (pendingPasswordReset == null) {
            System.out.println("No active password reset. Use 'forget password' first.");
            return;
        }

        Matcher matcher = Regex.ANSWER.getMatcherRaw(text);
        matcher.matches();
        String answer = matcher.group("answer");

        if (!pendingPasswordReset.checkSecurityAnswer(answer)) {
            System.out.println("Incorrect answer. Returning to menu.");
            pendingPasswordReset = null;
            return;
        }

        awaitingNewPassword = true;
        System.out.println("Correct! Enter your new password:");
    }

    private void handleNewPassword(String text) {
        String newPassword = text.trim();

        if (!isStrongPassword(newPassword)) return;

        pendingPasswordReset.setPassword(newPassword);
        User.save();
        System.out.println("Password updated successfully. Please log in.");

        pendingPasswordReset = null;
        awaitingNewPassword = false;
    }

    private boolean isStrongPassword(String password) {
        String specialChars = "!#$%^&*()=+}{}[]|/\\:;'\"<>?";
        if (password.length() < 8) {
            System.out.println("Weak password: must be at least 8 characters.");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            System.out.println("Weak password: must contain a lowercase letter.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            System.out.println("Weak password: must contain an uppercase letter.");
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            System.out.println("Weak password: must contain a digit.");
            return false;
        }
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) {
                hasSpecial = true;
                break;
            }
        }
        if (!hasSpecial) {
            System.out.println("Weak password: must contain a special character.");
            return false;
        }
        return true;
    }

    private void handleMenuEnter(String text) {
        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
        matcher.matches();
        String menuName = matcher.group("menuname");

        if (menuName == "Main menu") {
            System.out.println("You can only enter the Main Menu after logging in.");
            return;
        }
        changeMenu(menuName);
    }

    @Override
    public void exitMenu() {
        changeMenu("SignUp Menu");
    }

    @Override
    public String showMenu() {
        return "[ Login Menu ]\nCommands:\n  login -u <username> -p <password> [-stay-logged-in]\n  forget password -u <username> -e <email>\n  menu enter <menu_name>\n  menu exit\n  menu show current";
    }
}