package controller.menus.authentication;

import controller.menus.Menu;
import model.game_exceptions.GameException;
import model.user_data.User;
import model.Regex;

import java.util.List;
import java.util.regex.Matcher;

import static model.App.currentMenu;

public class SignupMenu extends Menu {

    private static final List<String> SECURITY_QUESTIONS = List.of(
            "1. What is the name of your first pet?",
            "2. What city were you born in?",
            "3. What is your mother's maiden name?",
            "4. What was the name of your elementary school?",
            "5. What is your oldest sibling's middle name?"
    );

    private static final String SPECIAL_CHARS = "!#$%^&*()=+}{}[]|/\\:;'\"<>?";

    // the things we are working on currently
    private String pendingUsername, pendingPassword, pendingNickname, pendingEmail, pendingGender;
    private int pendingQuestion = -1;
    private boolean isPendingSecurityAnswer = false;

    @Override
    public String getName() {
        return "SignUP Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (isPendingSecurityAnswer) {
            if (Regex.PICK_QUESTION.getMatcherRaw(text).matches()) {
                handlePickQuestion(text);
            } else {
                System.out.println("Please pick a security question first.");
            }
            return;
        }

        if (Regex.REGISTER.getMatcherRaw(text).matches()) {
            handleRegister(text);
        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {
            handleMenuEnter(text);
        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {
            System.out.println(showMenu());
        } else {
            throw new GameException("Invalid command");
        }
    }

    private void handleRegister(String text) {
        Matcher matcher = Regex.REGISTER.getMatcherRaw(text);
        matcher.matches();

        String username = matcher.group("username"), password = matcher.group("password"),
                passwordConfirm = matcher.group("passwordConfirm"), nickname = matcher.group("nickname");
        String email = matcher.group("email"), gender = matcher.group("gender");

        if (!(validateUsername(username) && validatePassword(password, passwordConfirm) && validateNickname(nickname)
                && validateEmail(email) && validateGender(gender))) return; // discrete structure

        pendingUsername = username;
        pendingPassword = password;
        pendingNickname = nickname;
        pendingEmail = email;
        pendingGender = gender;
        isPendingSecurityAnswer = true;

        System.out.println("Please pick a security question:");
        for (String q : SECURITY_QUESTIONS) {
            System.out.println(q);
        }
        System.out.println("Usage: pick question -q <number> -a <answer> -c <answer_confirm>");
    }

    private void handlePickQuestion(String text) {
        Matcher matcher = Regex.PICK_QUESTION.getMatcherRaw(text);
        matcher.matches();

        int questionNumber;
        try {
            questionNumber = Integer.parseInt(matcher.group("question"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid question number.");
            return;
        }

        if (questionNumber < 1 || questionNumber > SECURITY_QUESTIONS.size()) {
            System.out.println("Question number must be between 1 and " + SECURITY_QUESTIONS.size() + ".");
            return;
        }

        String answer = matcher.group("answer"),  answerConfirm = matcher.group("answerConfirm");

        if (!answer.equals(answerConfirm)) {
            System.out.println("Answers do not match. Please try again.");
            return;
        }

        User newUser = new User(pendingUsername, pendingPassword, pendingNickname, pendingEmail, pendingGender);
        newUser.setSecurityQuestion(SECURITY_QUESTIONS.get(questionNumber - 1), answer);
        User.addUser(newUser);

        isPendingSecurityAnswer = false;
        System.out.println("Account created successfully! Please log in.");
        currentMenu = new LoginMenu();
    }

    private boolean validateUsername(String username) {
        if (!username.matches("[a-zA-Z0-9\\-]+")) {
            System.out.println("Username can only contain letters, digits, and '-'.");
            return false;
        }
        if (User.usernameExists(username)) {
            System.out.println("Username already exists. Please choose a different one.");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password, String confirm) {
        if (password.length() < 8) {
            System.out.println("Weak password: must be at least 8 characters.");
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            System.out.println("Weak password: must contain at least one lowercase letter.");
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            System.out.println("Weak password: must contain at least one uppercase letter.");
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            System.out.println("Weak password: must contain at least one digit.");
            return false;
        }
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHARS.indexOf(c) >= 0) {
                hasSpecial = true;
                break;
            }
        }
        if (!hasSpecial) {
            System.out.println("Weak password: must contain at least one special character (!#$%^&*...).");
            return false;
        }
        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match. Please re-enter.");
            return false;
        }
        return true;
    }

    private boolean validateNickname(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 30) {
            System.out.println("Nickname must be between 3 and 30 characters.");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9]([a-zA-Z0-9._\\-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9\\-]*[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]*[a-zA-Z0-9])?)*\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email format.");
            return false;
        }
        if (email.contains("..")) {
            System.out.println("Invalid email: consecutive dots are not allowed.");
            return false;
        }
        for (char c : email.toCharArray()) {
            if (SPECIAL_CHARS.indexOf(c) >= 0 && c != '@' && c != '.' && c != '-' && c != '_') {
                System.out.println("Invalid email: contains illegal characters.");
                return false;
            }
        }
        return true;
    }

    private boolean validateGender(String gender) {
        if (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female")) {
            System.out.println("Gender must be 'male' or 'female'.");
            return false;
        }
        return true;
    }

    private void handleMenuEnter(String text) {
        Matcher matcher = Regex.MENU_ENTER.getMatcherRaw(text);
        matcher.matches();
        String menuName = matcher.group("menuName").toLowerCase().trim();
        if (menuName.equals("login menu") || menuName.equals("login")) {
            currentMenu = new LoginMenu();
        } else {
            System.out.println("You can only enter the Login Menu from here.");
        }
    }

    @Override
    public void exitMenu() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    @Override
    public String showMenu() {
        return "[ SignUP Menu ]\nCommands:\n  register -u <username> -p <password> <confirm> -n <nickname> -e <email> -g <gender>\n  menu enter <menu_name>\n  menu exit\n  menu show current";
    }
}