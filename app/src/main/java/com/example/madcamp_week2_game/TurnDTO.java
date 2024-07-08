package com.example.madcamp_week2_game;

public class TurnDTO {
    private int id;
    private int userId;
    private long startTime;
    private long endTime;
    private long turn_duration;

    public TurnDTO(int id, int userId, long startTime) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = 0;
        this.turn_duration = 1;
    }
    public int getId() { return id; }

    public int getUserId() {
        return userId;
    }

    public long getStartTime() { return startTime; }

    public long getEndTime() { return endTime; }

    public long getTurn_duration() {
        return turn_duration;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.turn_duration = endTime - startTime;
    }
}
