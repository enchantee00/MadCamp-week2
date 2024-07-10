package com.example.madcamp_week2_game;

public class RequestTurnStart {

    private int user_id;
    private int turn_duration;
    private String turn_start;

    public RequestTurnStart(int user_id, int turn_duration, String started_ts) {
        this.user_id = user_id;
        this.turn_duration = turn_duration;
        this.turn_start = started_ts;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTurn_duration() {
        return turn_duration;
    }

    public String getStarted_ts() {
        return turn_start;
    }
}
