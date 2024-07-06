package com.example.madcamp_week2_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
                loadFragment(new EmptyFragment());
                buttonLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
            }, 100);
        });
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
