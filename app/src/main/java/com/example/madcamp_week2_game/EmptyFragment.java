package com.example.madcamp_week2_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;


public class EmptyFragment extends Fragment {
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);
        textView = view.findViewById(R.id.textView);
        fetchData();
        return view;
    }

    private void fetchData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.10.7.97:80")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        retrofit2.Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, retrofit2.Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Code:" + response.code());
                    return;
                }

                List<Post> posts = response.body();

                for (Post post: posts) {
                    String content = "";
                    content += "ID : " + post.getId() + "\n";
                    content += "test : " + post.getTest() + "\n\n";

                    textView.append(content);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                textView.setText(t.getMessage());
            }
        });

    }
}