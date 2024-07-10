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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GameFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private UserDTO user = UserManager.getInstance().getUser();
    private static final String BASE_URL = "http://172.10.7.97:80/api/";

    private Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
    private GameApi gameApi = retrofit.create(GameApi.class);

    private int Raining;
    private int sky;
    private ImageView ground;

    private ImageView player;
    private ImageView itemSlowDown;
    private TextView itemSlowDownCountText;
    private ImageView itemNoBomb;
    private TextView itemNoBombCountText;
    private TextView itemTriplePoints;
    private TextView itemTriplePointsCountText;
    private ImageView itemBiggerFood;
    private TextView itemBiggerFoodCountText;
    private float dx;
    private float minX;
    private float maxX;
    private boolean isGameRunning = false;
    private boolean isGameStarted = false;
    private boolean isItemSlowDownActive = false;
    private boolean isItemNoBombActive = false;
    private boolean isItemTriplePointsActive = false;
    private boolean isItemBiggerFoodActive = false;
    private int itemSlowDownCount = user.getItem_slow_down_cnt();
    private int itemNoBombCount = user.getItem_no_bomb_cnt();
    private int itemTriplePointsCount = user.getItem_triple_points_cnt();
    private int itemBiggerFoodCount = user.getItem_big_size_cnt();
    private Handler handler = new Handler();
    private Random random = new Random();

    private int[] foodImages = {R.drawable.food1, R.drawable.food2, R.drawable.food3, R.drawable.food4};
    private int bombImage = R.drawable.bomb;
    private RelativeLayout rootLayout;
    private Runnable foodDropRunnable;
    private TextView scoreText;
    private TextView levelText;
    private int score = 0;
    private int lives = 5;
    private int level = 1;
    private long dropInterval = 1000;
    private long dropSpeed = 3000;
    private ImageView[] lifeImages;
    private int pointMultiplier = 1;

    private TurnDTO turn;
    private int turn_id = 0;
    private long startTime;
    private long endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        rootLayout = view.findViewById(R.id.rootLayout);
        scoreText = view.findViewById(R.id.scoreText);
        levelText = view.findViewById(R.id.levelText);

        player = view.findViewById(R.id.player);
        ground = view.findViewById(R.id.ground);

        Bundle args = getArguments();
        if (args != null) {
            Raining = args.getInt("Raining");
            sky = args.getInt("sky");
        }
        updateBackgroundAndGround();

        itemSlowDown = view.findViewById(R.id.itemSlowDown);
        itemSlowDownCountText = view.findViewById(R.id.itemSlowDownCountText);
        itemNoBomb = view.findViewById(R.id.itemNoBomb);
        itemNoBombCountText = view.findViewById(R.id.itemNoBombCountText);
        itemTriplePoints = view.findViewById(R.id.itemTriplePoints);
        itemTriplePointsCountText = view.findViewById(R.id.itemTriplePointsCountText);
        itemBiggerFood = view.findViewById(R.id.itemBiggerFood);
        itemBiggerFoodCountText = view.findViewById(R.id.itemBiggerFoodCountText);

        updateItemCount(itemSlowDown, itemSlowDownCountText, itemSlowDownCount);
        updateItemCount(itemNoBomb, itemNoBombCountText, itemNoBombCount);
        itemTriplePointsCountText.setText("x" + itemTriplePointsCount);
        updateItemCount(itemBiggerFood, itemBiggerFoodCountText, itemBiggerFoodCount);

        updateItemState(itemSlowDown, itemSlowDownCountText, itemSlowDownCount, R.drawable.item_slowdown, R.drawable.item_slowdown_gray);
        updateItemState(itemNoBomb, itemNoBombCountText, itemNoBombCount, R.drawable.item_nobomb, R.drawable.item_nobomb_gray);
        if (itemTriplePointsCount > 0) {
            itemTriplePoints.setTextColor(getResources().getColor(android.R.color.black));
            itemTriplePointsCountText.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            itemTriplePoints.setTextColor(getResources().getColor(android.R.color.darker_gray));
            itemTriplePointsCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        updateItemState(itemBiggerFood, itemBiggerFoodCountText, itemBiggerFoodCount, R.drawable.item_biggerfood, R.drawable.item_biggerfood_gray);

        TextView startButton = view.findViewById(R.id.startButton);
        TextView restartButton = view.findViewById(R.id.restartButton);
        TextView stopButton = view.findViewById(R.id.stopButton);
        // backToMainButton = view.findViewById(R.id.backToMainButton);

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
                        if (!isItemNoBombActive) {
                            player.setImageResource(R.drawable.character_mouth_open);
                        } else {
                            player.setImageResource(R.drawable.character_shield);
                            ViewGroup.LayoutParams originalParams = player.getLayoutParams();
                            originalParams.width = convertDpToPx(150);
                            originalParams.height = convertDpToPx(150);
                            player.setLayoutParams(originalParams);
                        }
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
                        if (isItemNoBombActive) {
                            player.setImageResource(R.drawable.character);
                            ViewGroup.LayoutParams originalParams = player.getLayoutParams();
                            originalParams.width = convertDpToPx(100);
                            originalParams.height = convertDpToPx(100);
                            player.setLayoutParams(originalParams);
                        }
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

        itemSlowDown.setOnClickListener(v -> {
            if (!isItemSlowDownActive && itemSlowDownCount > 0) {
                isItemSlowDownActive = true;
                itemSlowDownCount--;
                itemSlowDown.setImageResource(R.drawable.item_slowdown_gray);
                itemSlowDownCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                updateItemCount(itemSlowDown, itemSlowDownCountText, itemSlowDownCount);

                slowDownFoods();

                long pressed_ms = System.currentTimeMillis();
                long pressed_ts = pressed_ms - turn.getStartTime();
                Log.d("millisecs - item1", String.valueOf(pressed_ts));

                if (user != null) {
                    RequestItemClick requestItemClick = new RequestItemClick(user.getId(), turn.getId(), pressed_ts, 1);
                    Call<Void> call = gameApi.itemClick(requestItemClick);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        itemNoBomb.setOnClickListener(v -> {
            if (level >= 4 && !isItemNoBombActive && itemNoBombCount > 0) {
                isItemNoBombActive = true;
                itemNoBombCount--;
                itemNoBomb.setImageResource(R.drawable.item_nobomb_gray);
                itemNoBombCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                updateItemCount(itemNoBomb, itemNoBombCountText, itemNoBombCount);

                activateNoBombShield();

                long pressed_ms = System.currentTimeMillis();
                long pressed_ts = pressed_ms - turn.getStartTime();

                if (user != null) {
                    RequestItemClick requestItemClick = new RequestItemClick(user.getId(), turn.getId(), pressed_ts, 2);
                    Call<Void> call = gameApi.itemClick(requestItemClick);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        itemTriplePoints.setOnClickListener(v -> {
            if (!isItemTriplePointsActive && itemTriplePointsCount > 0) {

                isItemTriplePointsActive = true;
                itemTriplePointsCount--;
                itemTriplePoints.setTextColor(getResources().getColor(android.R.color.darker_gray));
                itemTriplePointsCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));

                itemTriplePointsCountText.setText("x" + itemTriplePointsCount);
                //itemTriplePoints.setVisibility(View.GONE);
                //itemTriplePointsCountText.setVisibility(View.GONE);


                activateTriplePoints();

                long pressed_ms = System.currentTimeMillis();
                long pressed_ts = pressed_ms - turn.getStartTime();

                if (user != null) {
                    RequestItemClick requestItemClick = new RequestItemClick(user.getId(), turn.getId(), pressed_ts, 4);
                    Call<Void> call = gameApi.itemClick(requestItemClick);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        itemBiggerFood.setOnClickListener(v -> {
            if (!isItemBiggerFoodActive && itemBiggerFoodCount > 0) {
                isItemBiggerFoodActive = true;
                itemBiggerFoodCount--;
                itemBiggerFood.setImageResource(R.drawable.item_biggerfood_gray);
                itemBiggerFoodCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                updateItemCount(itemBiggerFood, itemBiggerFoodCountText, itemBiggerFoodCount);
                activateBiggerFood();

                long pressed_ms = System.currentTimeMillis();
                long pressed_ts = pressed_ms - turn.getStartTime();

                if (user != null) {
                    RequestItemClick requestItemClick = new RequestItemClick(user.getId(), turn.getId(), pressed_ts, 3);
                    Call<Void> call = gameApi.itemClick(requestItemClick);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        player.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            minX = rootLayout.getPaddingLeft();
            maxX = rootLayout.getWidth() - player.getWidth() - rootLayout.getPaddingRight();
            Log.d(TAG, "minX: " + minX + ", maxX: " + maxX);
        });

        return view;
    }

    private void updateBackgroundAndGround() {
        if (Raining != 0) {
            if (Raining == 1 || Raining == 2 || Raining == 5) {
//                rootLayout.setBackgroundResource(R.drawable.background_rainy);
            } else if (Raining == 3 || Raining == 6 || Raining == 7) {
//                rootLayout.setBackgroundResource(R.drawable.background_snowy);
                ground.setImageResource(R.drawable.ground_snow);
            }
        } else {
            if (sky == 1) {
//                rootLayout.setBackgroundResource(R.drawable.background_sunny);
            } else if (sky == 3 || sky == 4) {
//                rootLayout.setBackgroundResource(R.drawable.background_cloudy);
            }
        }
        if (Raining == 0 && sky == 0) {
            rootLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void initializeGame() {
        score = 0;
        lives = 5;
        level = 1;
        dropInterval = 1000;
        pointMultiplier = 1;
        updateScore();
        updateLives();
        updateLevel(true);
    }

    private void startGame() {
        isGameStarted = true;
        isGameRunning = true;
        initializeGame();
        startTime = System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String started_ts = now.format(formatter);
        Log.d(TAG, started_ts);

        if (user != null) {
            RequestTurnStart requestTurnStart = new RequestTurnStart(user.getId(), 1, started_ts);
            Call<JsonObject> call = gameApi.startTurn(requestTurnStart);
            Log.d("game start request", String.valueOf(call));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("game start response", response.toString());
                    if (response.isSuccessful()) {
                        JsonObject jsonResponse = response.body();
                        turn = new TurnDTO(jsonResponse.get("turn_id").getAsInt(), user.getId(), startTime);
                    } else {
                        Toast.makeText(getContext(), "Failed to start game", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Log.d(TAG, "Game started");
        foodDropRunnable = new Runnable() {
            public void run() {
                Log.d(TAG, "Start Dropping Food - foodDropRunnable");
                if (isGameRunning) {
                    dropFood();
                    handler.postDelayed(this, dropInterval);
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
        endTime = System.currentTimeMillis();
        turn.setEndTime(endTime);
        Log.d(TAG, "Game duration: " + turn.getTurn_duration() + " milliseconds");
        user.setBest_score(score);
        user.addPoint(score);
        user.setItem_slow_down_cnt(itemSlowDownCount);
        user.setItem_no_bomb_cnt(itemNoBombCount);
        user.setItem_triple_points_cnt(itemTriplePointsCount);
        user.setItem_big_size_cnt(itemBiggerFoodCount);

        if (user != null) {
            RequestTurnEnd requestTurnEnd = new RequestTurnEnd(
                    user.getId(),
                    turn.getId(),
                    turn.getTurn_duration(),
                    user.getItem_slow_down_cnt(),
                    user.getItem_no_bomb_cnt(),
                    user.getItem_big_size_cnt(),
                    user.getItem_triple_points_cnt(),
                    score
            );
            Call<Void> call = gameApi.endTurn(requestTurnEnd);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Log.d(TAG, "Game Item Counts = " + itemSlowDownCount +", "+ itemNoBombCount +", "+ itemTriplePointsCount +", " + itemBiggerFoodCount);
        Log.d(TAG, "User Item Counts = " + user.getItem_slow_down_cnt() +", "+ user.getItem_no_bomb_cnt() +", "+ user.getItem_triple_points_cnt() +", " + user.getItem_big_size_cnt());

        handler.removeCallbacks(foodDropRunnable);

        for (int i = rootLayout.getChildCount() - 1; i >= 0; i--) {
            View view = rootLayout.getChildAt(i);
            if (view.getTag() != null && view.getTag().equals("food")) {
                rootLayout.removeView(view);
                Log.d(TAG, "Food removed");
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
        int foodImage;
        if (level >= 4 && random.nextInt(5) == 0) {
            foodImage = bombImage;
            food.setTag("bomb");
        } else {
            foodImage = foodImages[random.nextInt(foodImages.length)];
            food.setTag("food");
        }
        food.setImageResource(foodImage);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                food.getTag().equals("food") && isItemBiggerFoodActive ? 200 : 100,
                food.getTag().equals("food") && isItemBiggerFoodActive ? 200 : 100
        );
        params.leftMargin = (int) minX + random.nextInt((int) (maxX - minX));
        food.setLayoutParams(params);

        food.setTranslationZ(-1);
        rootLayout.addView(food);
        Log.d(TAG, "Food added: " + foodImage);

        handler.post(new Runnable() {
            @Override
            public void run() {
                food.animate()
                        .translationY(rootLayout.getHeight() + 100)
                        .setDuration(dropSpeed)
                        .withEndAction(() -> {
                            rootLayout.removeView(food);
                            Log.d(TAG, "Food removed");
                        })
                        .start();
                Log.d(TAG, "Current drop speed: " + dropSpeed);
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isColliding(food, player)) {
                    if (food.getTag().equals("food")){
                        score += 10 * pointMultiplier;
                        updateScore();
                        updateLevel(false);
                    } else if (food.getTag().equals("bomb")) {
                        if (!isItemNoBombActive) {
                            loseLife();
                        }
                    }
                    food.animate().cancel();
                    rootLayout.removeView(food);
                } else if (food.getY() >= rootLayout.getHeight() - food.getHeight()) {
                    if (food.getTag().equals("food")){
                        loseLife();
                    }
                    rootLayout.removeView(food);
                } else if (food.getParent() != null) {
                    handler.postDelayed(this, 50);
                }
            }
        }, 50);
    }


    private void slowDownFoods() {
        int initialLevel = level;
        long initialSpeed = dropSpeed;

        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View view = rootLayout.getChildAt(i);
            if (view.getTag() != null) {
                float remainingDistance = rootLayout.getHeight() - view.getY();
                long newDuration = (long) (remainingDistance / rootLayout.getHeight() * initialSpeed * 2);
                view.animate().cancel();
                view.animate().translationY(rootLayout.getHeight() + 100).setDuration(newDuration).start();
            }
        }
        Log.d(TAG, "initial drop speed: " + dropSpeed);
        dropSpeed = (long) (initialSpeed * 2);
        Log.d(TAG, "changed drop speed: " + dropSpeed);

        handler.postDelayed(() -> {
            isItemSlowDownActive = false;
            for (int i = 0; i < rootLayout.getChildCount(); i++) {
                View view = rootLayout.getChildAt(i);
                if (view.getTag() != null) {
                    float remainingDistance = rootLayout.getHeight() - view.getY();
                    long newDuration = (long) (remainingDistance / rootLayout.getHeight() * dropSpeed / 2);
                    view.animate().cancel();
                    view.animate().translationY(rootLayout.getHeight() + 100).setDuration(newDuration).start();
                }
            }
            Log.d(TAG, "current level: " + level + "\nslow down activated drop speed: " + dropSpeed);
            changeSpeed();
            Log.d(TAG, "current level: " + level + "\nslow down deactivated drop speed: " + dropSpeed);
            updateItemState(itemSlowDown, itemSlowDownCountText, itemSlowDownCount, R.drawable.item_slowdown, R.drawable.item_slowdown_gray);
        }, 10000); // for 10 seconds
    }

    private void activateNoBombShield() {
        player.setImageResource(R.drawable.character_shield);

        ViewGroup.LayoutParams params = player.getLayoutParams();
        params.width = convertDpToPx(150);
        params.height = convertDpToPx(150);
        player.setLayoutParams(params);

        handler.postDelayed(() -> {
            isItemNoBombActive = false;
            player.setImageResource(R.drawable.character_mouth_open);
            ViewGroup.LayoutParams originalParams = player.getLayoutParams();
            originalParams.width = convertDpToPx(100);
            originalParams.height = convertDpToPx(100);
            player.setLayoutParams(originalParams);
            updateItemState(itemNoBomb, itemNoBombCountText, itemNoBombCount, R.drawable.item_nobomb, R.drawable.item_nobomb_gray);
        }, 10000);
    }

    private void activateTriplePoints() {
        pointMultiplier = 3;

        handler.postDelayed(() -> {
            pointMultiplier = 1;
            isItemTriplePointsActive = false;
            if (itemTriplePointsCount > 0) {
                itemTriplePoints.setTextColor(getResources().getColor(android.R.color.black));
                itemTriplePointsCountText.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                itemTriplePoints.setTextColor(getResources().getColor(android.R.color.darker_gray));
                itemTriplePointsCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }, 10000);
    }

    private void activateBiggerFood() {
        isItemBiggerFoodActive = true;
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View view = rootLayout.getChildAt(i);
            if (view.getTag() != null && view.getTag().equals("food")) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width *= 2;
                params.height *= 2;
                view.setLayoutParams(params);
            }
        }

        handler.postDelayed(() -> {
            for (int i = 0; i < rootLayout.getChildCount(); i++) {
                View view = rootLayout.getChildAt(i);
                if (view.getTag() != null && view.getTag().equals("food")) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width /= 2;
                    params.height /= 2;
                    view.setLayoutParams(params);
                }
            }
            isItemBiggerFoodActive = false;
            updateItemState(itemBiggerFood, itemBiggerFoodCountText, itemBiggerFoodCount, R.drawable.item_biggerfood, R.drawable.item_biggerfood_gray);
        }, 10000);
    }

    private void updateItemCount(ImageView item, TextView itemCountText, int itemCount) {
        itemCountText.setText("x" + itemCount);
        //item.setVisibility(View.GONE);
        //itemCountText.setVisibility(View.GONE);
    }

    private void updateItemState(ImageView item, TextView itemCountText, int itemCount, int imageResId, int imageResIdGray) {
        if (itemCount > 0) {
            item.setImageResource(imageResId);
            itemCountText.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            item.setImageResource(imageResIdGray);
            itemCountText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private void updateLives() {
        for (int i = 0; i < lifeImages.length; i++) {
            lifeImages[i].setVisibility(i < lives ? View.VISIBLE : View.GONE);
        }
    }

    private void updateLevel(boolean isInitialized) {
        if (isInitialized) {
            levelText.setText("Lv." + level);
            return;
        }

        int newLevel;if (score >= 1500) {
            newLevel = 5;
        } else if (score >= 1000) {
            newLevel = 4;
        } else if (score >= 600) {
            newLevel = 3;
        } else if (score >= 300) {
            newLevel = 2;
        } else {
            newLevel = 1;
        }

        if (newLevel != level) {
            level = newLevel;
            levelText.setText("Lv." + level);
            changeSpeed();
        }
    }

    private void changeSpeed() {
        Log.d(TAG, "drop speed before being changed: " + dropSpeed);
        switch (level) {
            case 1:
                dropSpeed = 3000;
                dropInterval = 1000;
                break;
            case 2:
                dropSpeed = 2500;
                dropInterval = 800;
                break;
            case 3:
                dropSpeed = 2000;
                dropInterval = 600;
                break;
            case 4:
                dropSpeed = 1500;
                dropInterval = 600;
                break;
            case 5:
                dropSpeed = 1300;
                dropInterval = 500;
                break;
        }
        Log.d(TAG, "drop speed after being changed: " + dropSpeed);
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
        // backToMainButton.setTextColor(getResources().getColor(android.R.color.white));
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

    private int convertDpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}