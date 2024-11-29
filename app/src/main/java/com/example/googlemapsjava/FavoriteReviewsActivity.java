// FavoriteReviewsActivity.java
package com.example.googlemapsjava;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteReviewsActivity extends AppCompatActivity {

    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private SharedPreferences prefs;

    private List<Review> filterReviewsByObjectId(String objectId) {
        List<Review> filteredReviews = new ArrayList<>();
        for (Review review : reviewList) {
            if (review.getObjectId().equals(objectId)) {
                filteredReviews.add(review);
            }
        }
        return filteredReviews;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_reviews);

        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("reviews", MODE_PRIVATE);
        reviewList = loadReviews();

        // Получаем objectId из интента
        String objectId = getIntent().getStringExtra("objectId");
        List<Review> filteredReviews = filterReviewsByObjectId(objectId);

        reviewAdapter = new ReviewAdapter(filteredReviews);
        reviewsRecyclerView.setAdapter(reviewAdapter);
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
}
