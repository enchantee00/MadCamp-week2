package com.example.madcamp_week2_game;

import com.google.gson.annotations.SerializedName;

public class ForecastItem {
    @SerializedName("baseDate")
    private String baseDate;

    @SerializedName("baseTime")
    private String baseTime;

    @SerializedName("category")
    private String category;

    @SerializedName("fcstDate")
    private String fcstDate;

    @SerializedName("fcstTime")
    private String fcstTime;

    @SerializedName("fcstValue")
    private String fcstValue;

    @SerializedName("nx")
    private int nx;

    @SerializedName("ny")
    private int ny;

    // Getters and setters
    public String getBaseDate() { return baseDate; }
    public String getBaseTime() { return baseTime; }
    public String getCategory() { return category; }
    public String getFcstDate() { return fcstDate; }
    public String getFcstTime() { return fcstTime; }
    public String getFcstValue() { return fcstValue; }
    public int getNx() { return nx; }
    public int getNy() { return ny; }
}
