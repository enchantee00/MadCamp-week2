package com.example.madcamp_week2_game;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PATCH;

import java.util.List;

public interface GameApi {
    @GET("api/users")
    Call<List<Post>> getPosts();

    @POST("app/login")
    Call<JsonObject> login(@Body RequestLogin loginRequest);

    @POST("app/turn/item/use")
    Call<Void> itemClick(@Body RequestItemClick requestItemClick);

    @POST("app/turn/start")
    Call<JsonObject> startTurn(@Body RequestTurnStart requestTurnStart);

    @PATCH("app/turn/end")
    Call<Void> endTurn(@Body RequestTurnEnd requestTurnEnd);

    @PATCH("app/item/buy")
    Call<Void> buyItem(@Body RequestBuyItem requestBuyItem);
}
