package model.singleton;

public class User {
    public String name;
    private String password;
    public UserState userState = new UserState();
}
