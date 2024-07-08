package com.example.madcamp_week2_game;

public class RequestBuyItem {

    private int user_id;
    private int item_id;
    private int cost;
    private int count;

    public RequestBuyItem(int user_id, int item_id, int cost, int count) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.cost = cost;
        this.count = count;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public int getCost() {
        return cost;
    }

    public int getCount() {
        return count;
    }
}
