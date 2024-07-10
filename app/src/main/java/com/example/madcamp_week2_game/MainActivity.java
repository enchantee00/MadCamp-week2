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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class MainActivity extends AppCompatActivity {

    private int Raining;
    private int sky;
    private int curr_lat = 36;
    private int curr_lon = 127;

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
                RelativeLayout mainLayout = findViewById(R.id.main_layout);
                mainLayout.setBackgroundResource(0);
                mainLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
                loadFragment(new RecordFragment());
                buttonLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }, 100);
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                curr_lat = (int) location.getLatitude();
                curr_lon = (int) location.getLongitude();
            }
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
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body();
                        Log.d("MainActivity_Location", "response" + String.valueOf(jsonResponse));
                        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                        String itemsArrayString  = jsonObject.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonArray("item").toString();

                        Gson gson = new Gson();
                        JsonArray jsonArray = JsonParser.parseString(itemsArrayString).getAsJsonArray();
                        Type listType = new TypeToken<List<ForecastItem>>() {}.getType();
                        List<ForecastItem> forecastItems = gson.fromJson(jsonArray, listType);

                        ForecastItem earliestSkyItem = null;
                        for (ForecastItem item : forecastItems) {
                            if ("SKY".equals(item.getCategory())) {
                                if (earliestSkyItem == null || item.getBaseTime().compareTo(earliestSkyItem.getBaseTime()) < 0) {
                                    earliestSkyItem = item;
                                }
                            }
                        }

                        ForecastItem earliestPtyItem = null;
                        for (ForecastItem item : forecastItems) {
                            if ("PTY".equals(item.getCategory())) {
                                if (earliestPtyItem == null || item.getBaseTime().compareTo(earliestPtyItem.getBaseTime()) < 0) {
                                    earliestPtyItem = item;
                                }
                            }
                        }

                        if (earliestPtyItem != null) {
                            Raining = Integer.parseInt(earliestPtyItem.getFcstValue());
                        } else {
                            Raining = 0;
                        }

                        if (earliestSkyItem != null) {
                            sky = Integer.parseInt(earliestSkyItem.getFcstValue());
                        } else {
                            sky = 0;
                        }

                        updateBackground();
                        Log.d("MainActivity_Location", "sky: " + String.valueOf(sky) + " raining: " + String.valueOf(Raining));
                        Log.d("MainActivity_Location", "response from json: " + itemsArrayString);
                    } else {
                        Raining = 0;
                        sky = 0;
                        Log.d("MainActivity_Location", "Fail to get weather information");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Raining = 0;
                    sky = 0;
                    Log.d("MainActivity_Location", "Error: " + t.getMessage());
                }
            });
        } else {
            Raining = 0;
            sky = 0;
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }


    }

    private void updateBackground() {
        // for testing
        //Raining = 6;
        //sky = 3;
        //
        RelativeLayout mainLayout = findViewById(R.id.main_layout);
        if (Raining != 0) {
            if (Raining == 1 || Raining == 2 || Raining == 5) {
                mainLayout.setBackgroundResource(R.drawable.background_rainy);
            } else if (Raining == 3 || Raining == 6 || Raining == 7) {
                mainLayout.setBackgroundResource(R.drawable.background_snowy);
            }
        } else {
            if (sky == 1) {
                mainLayout.setBackgroundResource(R.drawable.background_sunny);
            } else if (sky == 3 || sky == 4) {
                mainLayout.setBackgroundResource(R.drawable.background_cloudy);
            }
        }
        if (Raining == 0 && sky == 0) {
            mainLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void loadFragment(Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt("Raining", Raining);
        args.putInt("sky", sky);
        fragment.setArguments(args);

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
            updateBackground();
        } else {
            super.onBackPressed();
        }
    }

}
