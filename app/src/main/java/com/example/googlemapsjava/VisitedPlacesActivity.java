package com.example.googlemapsjava;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class VisitedPlacesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_places);

        // Получите список посещенных мест из VisitedPlacesManager
        ArrayList<ObjectInfo> visitedPlaces = VisitedPlacesManager.getInstance().getVisitedPlaces();

        // Настройка RecyclerView для отображения списка посещенных мест
        RecyclerView recyclerView = findViewById(R.id.visitedPlacesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Передаем обработчик клика в адаптер
        VisitedPlacesAdapter adapter = new VisitedPlacesAdapter(visitedPlaces, this::openObjectInfo);
        recyclerView.setAdapter(adapter);
    }

    private void openObjectInfo(ObjectInfo place) {
        Intent intent = new Intent(VisitedPlacesActivity.this, ObjectInfoActivity.class);
        intent.putExtra("title", place.getTitle());
        intent.putExtra("workingHours", place.getWorkingHours());
        intent.putExtra("details", place.getDetails());
        intent.putExtra("imageResourceId", place.getImageResourceId());

        // Передача координат, если они существуют
        LatLng position = place.getPosition();
        if (position != null) {
            intent.putExtra("latitude", position.latitude);
            intent.putExtra("longitude", position.longitude);
        }

        startActivity(intent);
    }

}

