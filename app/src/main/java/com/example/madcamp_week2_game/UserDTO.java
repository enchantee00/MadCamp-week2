package com.example.madcamp_week2_game;

public class UserDTO {

    private int id;
    private String username;
    private int best_score;
    private int point;
    private int item_slow_down_cnt;
    private int item_no_bomb_cnt;
    private int item_triple_points_cnt;
    private int item_big_size_cnt;

    public UserDTO(
            int id,
            String username,
            int best_score,
            int point,
            int item_slow_down_cnt,
            int item_no_bomb_cnt,
            int item_triple_points_cnt,
            int item_big_size_cnt
    ) {
        this.id = id;
        this.username = username;
        this.best_score = best_score;
        this.point = point;
        this.item_slow_down_cnt = item_slow_down_cnt;
        this.item_no_bomb_cnt = item_no_bomb_cnt;
        this.item_triple_points_cnt = item_triple_points_cnt;
        this.item_big_size_cnt = item_big_size_cnt;
    }
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getBest_score() {
        return best_score;
    }

    public int getPoint() {
        return point;
    }

    public int getItem_slow_down_cnt() {
        return item_slow_down_cnt;
    }

    public int getItem_no_bomb_cnt() {
        return item_no_bomb_cnt;
    }

    public int getItem_triple_points_cnt() {
        return item_triple_points_cnt;
    }

    public int getItem_big_size_cnt() {
        return item_big_size_cnt;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setBest_score(int score) {
        if (this.best_score < score) {
            this.best_score = score;
        }
    }

    public void addPoint(int score) {
        this.point += score;
    }

    // use when game is finished
    public void setItem_slow_down_cnt(int newCount) {
        this.item_slow_down_cnt = newCount;
    }
    public void setItem_no_bomb_cnt(int newCount) {
        this.item_no_bomb_cnt = newCount;
    }
    public void setItem_triple_points_cnt(int newCount) {
        this.item_triple_points_cnt = newCount;
    }
    public void setItem_big_size_cnt(int newCount) {
        this.item_big_size_cnt = newCount;
    }

    // use when user buy new items
    public void addItem_slow_down(int newCount) {
        this.item_slow_down_cnt += newCount;
    }
    public void addItem_no_bomb(int newCount) {
        this.item_no_bomb_cnt += newCount;
    }
    public void addItem_triple_points(int newCount) {
        this.item_triple_points_cnt += newCount;
    }
    public void addItem_big_size(int newCount) {
        this.item_big_size_cnt += newCount;
    }

}
