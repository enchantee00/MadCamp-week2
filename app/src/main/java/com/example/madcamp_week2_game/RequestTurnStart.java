package com.example.madcamp_week2_game;

public class RequestTurnStart {

    private int user_id;
    private int turn_duration;

    public RequestTurnStart(int user_id, int turn_duration) {
        this.user_id = user_id;
        this.turn_duration = turn_duration;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTurn_duration() {
        return turn_duration;
    }
}
