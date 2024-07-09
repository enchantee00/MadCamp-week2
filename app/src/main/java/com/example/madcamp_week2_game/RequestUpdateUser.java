package com.example.madcamp_week2_game;

public class RequestUpdateUser {
    private int user_id;
    private String name;

    public RequestUpdateUser(int user_id, String name) {
        this.user_id = user_id;
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }
}
