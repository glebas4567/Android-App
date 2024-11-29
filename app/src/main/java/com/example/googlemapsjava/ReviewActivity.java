// ReviewActivity.java
package com.example.googlemapsjava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private EditText reviewText;
    private RatingBar ratingBar;
    private Button submitButton;
    private EditText usernameEditText;
    private SharedPreferences prefs;
    private List<Review> reviewList;
    private String objectId; // Идентификатор объекта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewText = findViewById(R.id.review_text);
        ratingBar = findViewById(R.id.rating_bar);
        submitButton = findViewById(R.id.button_submit_review);
        usernameEditText = findViewById(R.id.username_edit_text);

        prefs = getSharedPreferences("reviews", MODE_PRIVATE);
        reviewList = loadReviews();

        // Получаем objectId из интента
        objectId = getIntent().getStringExtra("objectId");

        submitButton.setOnClickListener(v -> saveReview());
    }

    private List<Review> loadReviews() {
        String json = prefs.getString("reviews_list", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Review>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private void saveReview() {
        String text = reviewText.getText().toString();
        float rating = ratingBar.getRating();
        String username = usernameEditText.getText().toString();

        if (!text.isEmpty() && !username.isEmpty() && rating > 0) {
            // Создаем новый отзыв, привязанный к objectId
            reviewList.add(new Review(text, rating, username, objectId));

            // Сохраняем обновлённый список в SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            String json = new Gson().toJson(reviewList);
            editor.putString("reviews_list", json);
            editor.apply();

            Toast.makeText(this, "Отзыв сохранён!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
    }

}
