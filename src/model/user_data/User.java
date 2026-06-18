package model.user_data;

import model.game_exceptions.GameException;
import model.news.News;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

// authentication and holding a reference to userState
public class User {

    private static final String SAVE_FILE = "users.txt";

    public static ArrayList<User> users = new ArrayList<>();
    public static User currentUser = null;

    public String username, passwordHash, nickname, email, gender, securityQuestion, securityAnswerHash;
    public boolean stayLoggedIn;
    public UserState userState;


    public User(String username, String password, String nickname, String email, String gender) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.stayLoggedIn = false;
        this.userState = new UserState(new News[0], 0, 0, 0);
    }
    private User(){}; // template

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded)
                hex.append(String.format("%02x", b)); // fill left with 0, min width 2 and in hex
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new GameException("SHA-256 not available");
        }
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    public void setSecurityQuestion(String question, String answer) {
        this.securityQuestion = question;
        this.securityAnswerHash = hashPassword(answer.toLowerCase().trim());
    }

    public boolean checkSecurityAnswer(String answer) {
        return this.securityAnswerHash.equals(hashPassword(answer.toLowerCase().trim()));
    }

    public static User findByUsername(String username) {
        for (User user : users)
            if (user.username.equals(username)) return user;
        return null;
    }

    public static User findByEmail(String email) {
        for (User user : users)
            if (user.email.equals(email)) return user;
        return null;
    }

    public static boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }


    // username|passwordHash|nickname|email|gender|securityQuestion|securityAnswerHash|stayLoggedIn|lastLevel|diamonds|coins
    public static void load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try {
            for (String line : Files.readAllLines(Paths.get(SAVE_FILE))) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", -1); // because none of them has "|"
                if (parts.length < 11) continue;
                User user = new User();
                user.username = parts[0];
                user.passwordHash = parts[1];
                user.nickname = parts[2];
                user.email = parts[3];
                user.gender = parts[4];
                user.securityQuestion = parts[5];
                user.securityAnswerHash = parts[6];
                user.stayLoggedIn = Boolean.parseBoolean(parts[7]);
                int lastLevel = Integer.parseInt(parts[8]), diamonds = Integer.parseInt(parts[9]),
                        coins = Integer.parseInt(parts[10]);
                user.userState = new UserState(new News[0], lastLevel, diamonds, coins);
                users.add(user);
                if (user.stayLoggedIn) currentUser = user;
            }
        } catch (IOException e) {
            throw new GameException("Could not load users: " + e.getMessage());
        }
    }

    public static void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (User user : users)
                writer.println(
                        user.username + "|" + user.passwordHash + "|" + user.nickname + "|" + user.email + "|" +
                                user.gender + "|" +
                                (user.securityQuestion != null ? user.securityQuestion : "") + "|" +
                                (user.securityAnswerHash != null ? user.securityAnswerHash : "") + "|" +
                                user.stayLoggedIn + "|" +
                                user.userState.lastLevel() + "|" +
                                user.userState.diamonds() + "|" +
                                user.userState.coins()
                );

        } catch (IOException e) {
            throw new GameException("Could not save users: " + e.getMessage());
        }
    }

    public static void addUser(User user) {
        users.add(user);
        save();
    }
}