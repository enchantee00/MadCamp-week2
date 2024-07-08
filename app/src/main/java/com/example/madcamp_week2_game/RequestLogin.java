package com.example.madcamp_week2_game;

public class RequestLogin {

    private String username;
    private String password;

    public RequestLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
