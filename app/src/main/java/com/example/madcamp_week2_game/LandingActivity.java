package com.example.madcamp_week2_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    private ImageView food1, food2, food3, food4;
    private Handler handler = new Handler();
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        food1 = findViewById(R.id.food1);
        food2 = findViewById(R.id.food2);
        food3 = findViewById(R.id.food3);
        food4 = findViewById(R.id.food4);

        startBounceAnimation();

        handler.postDelayed(() -> {
            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

    private void startBounceAnimation() {
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        final ImageView[] images = {food1, food2, food3, food4};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < images.length; i++) {
                    if (i == currentImageIndex) {
                        images[i].startAnimation(bounce);
                    } else {
                        images[i].clearAnimation();
                    }
                }
                currentImageIndex = (currentImageIndex + 1) % images.length;
                handler.postDelayed(this, 600);
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}