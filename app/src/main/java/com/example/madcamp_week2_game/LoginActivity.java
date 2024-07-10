package com.example.madcamp_week2_game;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final String TAG = "LoginActivity";
    private static final String BASE_URL_SERVER = "http://172.10.7.97:80/api/";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    private String base_date;
    private String base_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> login());

        // get permission from user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Retrofit retrofit_server = RetrofitClient.getClient("http://172.10.7.97:80/api/");
        GameApi gameApi_server = retrofit_server.create(GameApi.class);
        RequestLogin requestLogin = new RequestLogin(username, password);

        Call<JsonObject> call = gameApi_server.login(requestLogin);
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Fail to login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Fail to login", Toast.LENGTH_SHORT).show();
            }
        });
        // UserDTO userDTO = new UserDTO(8,"daemo", 1385, 5130, 5, 5, 3, 3);

    }
}
