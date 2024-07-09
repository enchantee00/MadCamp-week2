package com.example.madcamp_week2_game;

public class RequestWeather {

    private String ServiceKey;
    private int pageNo;
    private int numOfRows;
    private String dataType;
    private String base_date;
    private String base_time;
    private int nx;
    private int ny;

    public RequestWeather(String ServiceKey, int pageNo, int numOfRows, String dataType, String base_date, String base_time, int nx, int ny) {
        this.ServiceKey = ServiceKey;
        this.pageNo = pageNo;
        this.numOfRows = numOfRows;
        this.dataType = dataType;
        this.base_date = base_date;
        this.base_time = base_time;
        this.nx = nx;
        this.ny = ny;
    }

}
