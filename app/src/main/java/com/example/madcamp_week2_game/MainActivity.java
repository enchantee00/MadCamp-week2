package com.example.madcamp_week2_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout playButtonLayout = findViewById(R.id.playButtonLayout);
        RelativeLayout recordButtonLayout = findViewById(R.id.recordButtonLayout);
        RelativeLayout buttonLayout = findViewById(R.id.buttonLayout);
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);

        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click_scale);

        playButtonLayout.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                loadFragment(new GameFragment());
                buttonLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }, 100);
        });

        recordButtonLayout.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                loadFragment(new RecordFragment());
                buttonLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }, 100);
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                int curr_lat = (int) location.getLatitude();
                int curr_lon = (int) location.getLongitude();
//                String serviceKey = "PycGIfIhmGOc4XtKLuXmEFB7Pkr%2B8vXpzmOzrT5rPpfWFaCCiF6iu59NcPM29P4zs4REaLoO6YedxenvGzIHAw%3D%3D";
                String serviceKey = "PycGIfIhmGOc4XtKLuXmEFB7Pkr+8vXpzmOzrT5rPpfWFaCCiF6iu59NcPM29P4zs4REaLoO6YedxenvGzIHAw==";

                // forecast time selection
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime closestForecastTime;
                LocalDateTime thisHourForecast = currentTime.truncatedTo(ChronoUnit.HOURS).plusMinutes(30);
                LocalDateTime previousHourForecast = currentTime.truncatedTo(ChronoUnit.HOURS).minusHours(1).plusMinutes(30);

                if (currentTime.isBefore(thisHourForecast)) {
                    closestForecastTime = previousHourForecast;
                } else {
                    closestForecastTime = thisHourForecast;
                }
                String base_date = closestForecastTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                String base_time = closestForecastTime.format(DateTimeFormatter.ofPattern("HHmm"));


                Retrofit retrofit_weather = new Retrofit.Builder()
                        .baseUrl("http://apis.data.go.kr/")
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                GameApi gameApi_weather = retrofit_weather.create(GameApi.class);

                Call<String> call = gameApi_weather.getWeather(serviceKey, 1, 1000, "JSON", base_date, base_time, curr_lat, curr_lon);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("MainActivity_Location", response.toString());
                        if (response.isSuccessful() && response.body() != null) {
                            String jsonResponse = response.body();
//                            if (jsonResponse.isJsonPrimitive()) {
//                                JsonPrimitive jsonPrimitive = jsonResponse.getAsJsonPrimitive();
//                                JsonObject convertedJson = new JsonObject();
//                                convertedJson.add("value", jsonPrimitive);
//                                Log.d("MainActivity_Location", "Converted response: " + convertedJson.toString());
//                            } else if (jsonResponse.isJsonObject()) {
//                                Log.d("MainActivity_Location", "response" + jsonResponse.getAsJsonObject().toString());
//                            }
                            Log.d("MainActivity_Location", "response" + String.valueOf(jsonResponse));
                        } else {
                            Log.d("MainActivity_Location", "Fail to get weather information");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("MainActivity_Location", "Error: " + t.getMessage());
                    }
                });
            } else {
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            RelativeLayout buttonLayout = findViewById(R.id.buttonLayout);
            FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
            buttonLayout.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}
