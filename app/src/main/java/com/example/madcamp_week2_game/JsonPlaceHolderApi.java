package com.example.madcamp_week2_game;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface JsonPlaceHolderApi {
    @GET("api/items")
    Call<List<Post>> getPosts();
}
