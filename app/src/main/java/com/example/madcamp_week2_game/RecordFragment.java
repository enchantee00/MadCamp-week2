package com.example.madcamp_week2_game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;

public class RecordFragment extends Fragment {

    private final String BASE_URL = "http://172.10.7.97:80/api/";
    private UserDTO user;

    private TextView userNameTextView;
    private TextView bestScoreTextView;
    private TextView pointTextView;

    private TextView itemSlowDownCountTextView;
    private TextView itemNoBombCountTextView;
    private TextView itemTriplePointsCountTextView;
    private TextView itemBiggerFoodCountTextView;

    private TextView itemSlowDownBuyCountTextView;
    private TextView itemNoBombBuyCountTextView;
    private TextView itemTriplePointsBuyCountTextView;
    private TextView itemBiggerFoodBuyCountTextView;

    private int itemSlowDownBuyCount = 0;
    private int itemNoBombBuyCount = 0;
    private int itemTriplePointsBuyCount = 0;
    private int itemBiggerFoodBuyCount = 0;

    private int itemSlowDownPrice = 100;
    private int itemNoBombPrice = 100;
    private int itemTriplePointsPrice = 100;
    private int itemBiggerFoodPrice = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        userNameTextView = view.findViewById(R.id.userNameTextView);
        bestScoreTextView = view.findViewById(R.id.bestScoreTextView);
        pointTextView = view.findViewById(R.id.pointTextView);

        itemSlowDownCountTextView = view.findViewById(R.id.itemSlowDownCountTextView);
        itemNoBombCountTextView = view.findViewById(R.id.itemNoBombCountTextView);
        itemTriplePointsCountTextView = view.findViewById(R.id.itemTriplePointsCountTextView);
        itemBiggerFoodCountTextView = view.findViewById(R.id.itemBiggerFoodCountTextView);

        TextView itemSlowDownPlus = view.findViewById(R.id.itemSlowDownPlus);
        TextView itemSlowDownMinus = view.findViewById(R.id.itemSlowDownMinus);
        itemSlowDownBuyCountTextView = view.findViewById(R.id.itemSlowDownBuyCount);

        TextView itemNoBombPlus = view.findViewById(R.id.itemNoBombPlus);
        TextView itemNoBombMinus = view.findViewById(R.id.itemNoBombMinus);
        itemNoBombBuyCountTextView = view.findViewById(R.id.itemNoBombBuyCount);

        TextView itemTriplePointsPlus = view.findViewById(R.id.itemTriplePointsPlus);
        TextView itemTriplePointsMinus = view.findViewById(R.id.itemTriplePointsMinus);
        itemTriplePointsBuyCountTextView = view.findViewById(R.id.itemTriplePointsBuyCount);

        TextView itemBiggerFoodPlus = view.findViewById(R.id.itemBiggerFoodPlus);
        TextView itemBiggerFoodMinus = view.findViewById(R.id.itemBiggerFoodMinus);
        itemBiggerFoodBuyCountTextView = view.findViewById(R.id.itemBiggerFoodBuyCount);

        Button buyItemSlowDownButton = view.findViewById(R.id.buyItemSlowDownButton);
        Button buyItemNoBombButton = view.findViewById(R.id.buyItemNoBombButton);
        Button buyItemTriplePointsButton = view.findViewById(R.id.buyItemTriplePointsButton);
        Button buyItemBiggerFoodButton = view.findViewById(R.id.buyItemBiggerFoodButton);

        // Load user data
        user = UserManager.getInstance().getUser();
        if (user != null) {
            userNameTextView.setText(user.getUsername());
            bestScoreTextView.setText("Best score: " + String.valueOf(user.getBest_score()));
            pointTextView.setText("Point: " + String.valueOf(user.getPoint()));
            updateItemCounts();
        }

        itemSlowDownPlus.setOnClickListener(v-> {
            itemSlowDownBuyCount++;
            itemSlowDownBuyCountTextView.setText(String.valueOf(itemSlowDownBuyCount));
        });
        itemSlowDownMinus.setOnClickListener(v-> {
            if (itemSlowDownBuyCount > 0) {
                itemSlowDownBuyCount--;
                itemSlowDownBuyCountTextView.setText(String.valueOf(itemSlowDownBuyCount));
            }
        });

        itemNoBombPlus.setOnClickListener(v-> {
            itemNoBombBuyCount++;
            itemNoBombBuyCountTextView.setText(String.valueOf(itemNoBombBuyCount));
        });
        itemNoBombMinus.setOnClickListener(v-> {
            if (itemNoBombBuyCount > 0) {
                itemNoBombBuyCount--;
                itemNoBombBuyCountTextView.setText(String.valueOf(itemNoBombBuyCount));
            }
        });

        itemTriplePointsPlus.setOnClickListener(v-> {
            itemTriplePointsBuyCount++;
            itemTriplePointsBuyCountTextView.setText(String.valueOf(itemTriplePointsBuyCount));
        });
        itemTriplePointsMinus.setOnClickListener(v-> {
            if (itemTriplePointsBuyCount > 0) {
                itemTriplePointsBuyCount--;
                itemTriplePointsBuyCountTextView.setText(String.valueOf(itemTriplePointsBuyCount));
            }
        });

        itemBiggerFoodPlus.setOnClickListener(v-> {
            itemBiggerFoodBuyCount++;
            itemBiggerFoodBuyCountTextView.setText(String.valueOf(itemBiggerFoodBuyCount));
        });
        itemBiggerFoodMinus.setOnClickListener(v-> {
            if (itemBiggerFoodBuyCount > 0) {
                itemBiggerFoodBuyCount--;
                itemBiggerFoodBuyCountTextView.setText(String.valueOf(itemBiggerFoodBuyCount));
            }
        });

        buyItemSlowDownButton.setOnClickListener(v -> buyItem("slowDown"));
        buyItemNoBombButton.setOnClickListener(v -> buyItem("noBomb"));
        buyItemTriplePointsButton.setOnClickListener(v -> buyItem("triplePoints"));
        buyItemBiggerFoodButton.setOnClickListener(v -> buyItem("biggerFood"));

        return view;
    }

    private void buyItem(String itemType) {
        int price = 0;
        int itemCount = 0;
        int itemId = 0;

        switch (itemType) {
            case "slowDown":
                price = itemSlowDownPrice * itemSlowDownBuyCount;
                itemCount = itemSlowDownBuyCount;
                itemId = 1;
                break;
            case "noBomb":
                price = itemNoBombPrice * itemNoBombBuyCount;
                itemCount = itemNoBombBuyCount;
                itemId = 2;
                break;
            case "triplePoints":
                price = itemTriplePointsPrice * itemTriplePointsBuyCount;
                itemCount = itemTriplePointsBuyCount;
                itemId = 4;
                break;
            case "biggerFood":
                price = itemBiggerFoodPrice * itemBiggerFoodBuyCount;
                itemCount = itemBiggerFoodBuyCount;
                itemId = 3;
                break;
        }

        if (user.getPoint() >= price && itemCount > 0) {
            user.addPoint(-price);
            pointTextView.setText("Points: " + String.valueOf(user.getPoint()));
            switch (itemType) {
                case "slowDown":
                    user.addItem_slow_down(itemSlowDownBuyCount);
                    itemSlowDownBuyCount = 0;
                    itemSlowDownBuyCountTextView.setText(String.valueOf(itemSlowDownBuyCount));
                    break;
                case "noBomb":
                    user.addItem_no_bomb(itemNoBombBuyCount);
                    itemNoBombBuyCount = 0;
                    itemNoBombBuyCountTextView.setText(String.valueOf(itemNoBombBuyCount));
                    break;
                case "triplePoints":
                    user.addItem_triple_points(itemTriplePointsBuyCount);
                    itemTriplePointsBuyCount = 0;
                    itemTriplePointsBuyCountTextView.setText(String.valueOf(itemTriplePointsBuyCount));
                    break;
                case "biggerFood":
                    user.addItem_big_size(itemBiggerFoodBuyCount);
                    itemBiggerFoodBuyCount = 0;
                    itemBiggerFoodBuyCountTextView.setText(String.valueOf(itemBiggerFoodBuyCount));
                    break;
            }
            updateItemCounts();

            RequestBuyItem requestBuyItem = new RequestBuyItem(user.getId(), itemId, price, itemCount);
            Retrofit retrofit = RetrofitClient.getClient(BASE_URL);

            GameApi gameApi = retrofit.create(GameApi.class);
            Call<Void> call = gameApi.buyItem(requestBuyItem);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Item purchased successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to purchase item", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), "Not enough points or invalid item count", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItemCounts() {
        itemSlowDownCountTextView.setText("x" + user.getItem_slow_down_cnt());
        itemNoBombCountTextView.setText("x" + user.getItem_no_bomb_cnt());
        itemTriplePointsCountTextView.setText("x" + user.getItem_triple_points_cnt());
        itemBiggerFoodCountTextView.setText("x" + user.getItem_big_size_cnt());
    }
}
