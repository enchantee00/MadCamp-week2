package com.example.madcamp_week2_game;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class GameFragment extends Fragment {

    private static final String TAG = "MainActivity";

    private ImageView player;
    private View ground;
    private float dx;
    private float minX;
    private float maxX;
    private boolean isGameRunning = false;
    private boolean isGameStarted = false;
    private Handler handler = new Handler();
    private Random random = new Random();

    private int[] foodImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3, R.drawable.food4};
    private RelativeLayout rootLayout;
    private Runnable foodDropRunnable;
    private TextView scoreText;
    private int score = 0;
    private int lives = 5;
    private ImageView[] lifeImages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        player = view.findViewById(R.id.player);
        ground = view.findViewById(R.id.ground);
        rootLayout = view.findViewById(R.id.rootLayout);
        scoreText = view.findViewById(R.id.scoreText);

        TextView startButton = view.findViewById(R.id.startButton);
        TextView restartButton = view.findViewById(R.id.restartButton);
        TextView stopButton = view.findViewById(R.id.stopButton);

        lifeImages = new ImageView[5];
        lifeImages[0] = view.findViewById(R.id.life1);
        lifeImages[1] = view.findViewById(R.id.life2);
        lifeImages[2] = view.findViewById(R.id.life3);
        lifeImages[3] = view.findViewById(R.id.life4);
        lifeImages[4] = view.findViewById(R.id.life5);

        player.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!isGameStarted) return false;

                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        dx = view.getX() - event.getRawX();
                        player.setImageResource(R.drawable.character_mouth_open);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dx;
                        if (newX < minX) {
                            newX = minX;
                        } else if (newX > maxX) {
                            newX = maxX;
                        }
                        view.animate()
                                .x(newX)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        player.setImageResource(R.drawable.character);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        Animation clickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.click_scale);

        startButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                Log.d(TAG, "Start button clicked");
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                startGame();
            }, 100);
        });

        restartButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                Log.d(TAG, "Restart button clicked");
                restartButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
                startGame();
            }, 100);
        });

        stopButton.setOnClickListener(v -> {
            v.startAnimation(clickAnimation);
            v.postDelayed(() -> {
                Log.d(TAG, "Stop button clicked");
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                stopGame();
            }, 100);
        });

        player.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            minX = rootLayout.getPaddingLeft();
            maxX = rootLayout.getWidth() - player.getWidth() - rootLayout.getPaddingRight();
            Log.d(TAG, "minX: " + minX + ", maxX: " + maxX);
        });

        return view;
    }
    private void initializeGame() {
        score = 0;
        lives = 5;
        updateScore();
        updateLives();
    }

    private void startGame() {
        isGameStarted = true;
        isGameRunning = true;
        initializeGame();

        Log.d(TAG, "Game started");
        foodDropRunnable = new Runnable() {
            public void run() {
                Log.d(TAG, "Start Dropping Food - foodDropRunnable");
                if (isGameRunning) {
                    dropFood();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(foodDropRunnable);
        getView().findViewById(R.id.gameOverLayout).setVisibility(View.GONE);
    }

    private void stopGame() {
        isGameStarted = false;
        isGameRunning = false;
        Log.d(TAG, "Game stopped");
        handler.removeCallbacks(foodDropRunnable);
        for (int i = rootLayout.getChildCount() - 1; i >= 0; i--) {
            View view = rootLayout.getChildAt(i);
            if (view.getTag() != null && view.getTag().equals("food")) {
                rootLayout.removeView(view);
            }
        }

        player.animate()
                .x(rootLayout.getWidth() / 2 - player.getWidth() / 2)
                .setDuration(0)
                .start();
        player.setImageResource(R.drawable.character);
        initializeGame();
    }

    private void dropFood() {
        if (!isGameRunning) return;

        final ImageView food = new ImageView(getContext());
        int foodImage = foodImages[random.nextInt(foodImages.length)];
        food.setImageResource(foodImage);
        food.setTag("food");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.leftMargin = (int) minX + random.nextInt((int) (maxX - minX));
        food.setLayoutParams(params);

        food.setTranslationZ(-1);
        rootLayout.addView(food);
        Log.d(TAG, "Food added: " + foodImage);

        food.animate()
                .translationY(rootLayout.getHeight() + 100)
                .setDuration(3000)
                .withEndAction(() -> {
                    rootLayout.removeView(food);
                    Log.d(TAG, "Food removed");
                })
                .start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isColliding(food, player)) {
                    score += 10;
                    updateScore();
                    food.animate().cancel(); // Cancel animation
                    rootLayout.removeView(food);
                } else if (food.getY() >= rootLayout.getHeight() - food.getHeight()) {
                    loseLife();
                } else if (food.getParent() != null) {
                    handler.postDelayed(this, 50);
                }
            }
        }, 50);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private void updateLives() {
        for (int i = 0; i < lifeImages.length; i++) {
            lifeImages[i].setVisibility(i < lives ? View.VISIBLE : View.GONE);
        }
    }

    private void loseLife() {
        if (lives > 0) {
            lives--;
            updateLives();
            if (lives == 0) {
                stopGame();
                gameOver();
            }
        }
    }

    private void gameOver() {
        isGameRunning = false;
        Log.d(TAG, "Game over");

        View gameOverLayout = getView().findViewById(R.id.gameOverLayout);
        gameOverLayout.setAlpha(0f);
        gameOverLayout.setVisibility(View.VISIBLE);
        gameOverLayout.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null);

        getView().findViewById(R.id.stopButton).setVisibility(View.GONE);
        getView().findViewById(R.id.restartButton).setVisibility(View.VISIBLE);
    }

    private boolean isColliding(View food, View player) {
        int[] foodPos = new int[2];
        int[] playerPos = new int[2];
        food.getLocationOnScreen(foodPos);
        player.getLocationOnScreen(playerPos);

        int foodLeft = foodPos[0];
        int foodRight = foodPos[0] + food.getWidth();
        int foodTop = foodPos[1];
        int foodBottom = foodPos[1] + food.getHeight();

        int playerLeft = playerPos[0];
        int playerRight = playerPos[0] + player.getWidth();
        int playerTop = playerPos[1];
        int playerBottom = playerPos[1] + player.getHeight();

        return foodLeft < playerRight &&
                foodRight > playerLeft &&
                foodTop < playerBottom &&
                foodBottom > playerTop;
    }
}