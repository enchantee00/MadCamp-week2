package com.example.madcamp_week2_game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String BASE_URL = "http://172.10.7.97:80/api/";

    private Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
    private GameApi gameApi = retrofit.create(GameApi.class);

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        RequestLogin requestLogin = new RequestLogin(username, password);
        Call<JsonObject> call = gameApi.login(requestLogin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    UserDTO userDTO = new UserDTO(
                            jsonResponse.get("user_id").getAsInt(),
                            jsonResponse.get("username").getAsString(),
                            jsonResponse.get("best_score").getAsInt(),
                            jsonResponse.get("point").getAsInt(),
                            jsonResponse.get("item_slow_down").getAsInt(),
                            jsonResponse.get("item_no_bomb").getAsInt(),
                            jsonResponse.get("item_triple_points").getAsInt(),
                            jsonResponse.get("item_big_size").getAsInt()
                    );
                    UserManager.getInstance().setUser(userDTO);
                } else {
                    Log.d(TAG, "Fail to login");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
        // UserDTO userDTO = new UserDTO(8,"daemo", 1385, 5130, 5, 5, 3, 3);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
