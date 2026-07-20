package model.user_data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.greenhouse.Greenhouse;
import model.news.News;
import view.GeneralPrinter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/// authentication and holds a reference to userState
public class User {

    private static final String SAVE_FILE = "Data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
        this.userState = new UserState(new ArrayList<>(), 0, 0, 0);
    }



    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : encoded)
                hex.append(String.format("%02x", b));

            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
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



    public static boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    public static void load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<User>>() {}.getType(); // to define the format we're getting from json
            ArrayList<User> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) users = loaded;
            for (User user : users)
                if (user.stayLoggedIn) setUser(user);

        } catch (IOException e) {
            GeneralPrinter.print("Could not load users: " + e.getMessage());
        }
    }

    public static void setUser(User user) {
        currentUser = user;
        model.App.currentUser = user;
        Greenhouse.getInstance()
                .load(user.userState.greenhousePots);
    }

    public static void save() {
        if (currentUser != null) {
            currentUser.userState.greenhousePots =
                    Greenhouse.getInstance().serialize();
        }

        try (Writer writer = new FileWriter(SAVE_FILE)) {
            GSON.toJson(users, writer);
        } catch (IOException e) {
            GeneralPrinter.print("Could not save users: " + e.getMessage());
        }
    }

    public static void addUser(User user) {
        users.add(user);
        save();
    }
}