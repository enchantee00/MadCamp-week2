package com.example.madcamp_week2_game;

public class RequestItemClick {

    private int user_id;
    private int turn_id;
    private long pressed_ts;
    private int item_id;

    public RequestItemClick(int user_id, int turn_id, long pressed_ts, int item_id) {
        this.user_id = user_id;
        this.turn_id = turn_id;
        this.pressed_ts = pressed_ts;
        this.item_id = item_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getTurn_id() {
        return turn_id;
    }

    public long getPressed_ts() {
        return pressed_ts;
    }

    public int getItem_id() {
        return item_id;
    }
}
