package com.example.googlemapsjava;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class FavoritePlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);

        ArrayList<ObjectInfo> favoritePlaces = FavoritePlacesManager.getInstance().getFavoritePlaces();

        RecyclerView recyclerView = findViewById(R.id.favoritePlacesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FavoritePlacesAdapter adapter = new FavoritePlacesAdapter(favoritePlaces, this::openObjectInfo);
        recyclerView.setAdapter(adapter);
    }

    private void openObjectInfo(ObjectInfo place) {
        Intent intent = new Intent(FavoritePlacesActivity.this, ObjectInfoActivity.class);
        intent.putExtra("title", place.getTitle());
        intent.putExtra("workingHours", place.getWorkingHours());
        intent.putExtra("details", place.getDetails());
        intent.putExtra("imageResourceId", place.getImageResourceId());

        LatLng position = place.getPosition();
        if (position != null) {
            intent.putExtra("latitude", position.latitude);
            intent.putExtra("longitude", position.longitude);
        }

        startActivity(intent);
    }
}