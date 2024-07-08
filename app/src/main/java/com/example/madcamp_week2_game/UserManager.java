package com.example.madcamp_week2_game;

public class UserManager {

    private static UserManager instance;
    private UserDTO user;

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
