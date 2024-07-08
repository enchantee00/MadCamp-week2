package com.example.madcamp_week2_game;

public class RequestTurnEnd {

    private int user_id;
    private int turn_id;
    private long turn_duration;
    private int item_slow_down_used;
    private int item_no_bomb_used;
    private int item_big_size_used;
    private int item_triple_points_used;
    private int score;

    public RequestTurnEnd(int user_id, int turn_id, long turn_duration, int item_slow_down_used, int item_no_bomb_used, int item_big_size_used, int item_triple_points_used, int score) {
        this.user_id = user_id;
        this.turn_id = turn_id;
        this.turn_duration = turn_duration;
        this.item_slow_down_used = item_slow_down_used;
        this.item_no_bomb_used = item_no_bomb_used;
        this.item_big_size_used = item_big_size_used;
        this.item_triple_points_used = item_triple_points_used;
        this.score = score;
    }

    private int getUser_id() { return this.user_id; }
}
