package com.example.googlemapsjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

public class ObjectInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_info);

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView workingHoursTextView = findViewById(R.id.workingHoursTextView);
        TextView detailsTextView = findViewById(R.id.detailsTextView);
        ImageView objectImageView = findViewById(R.id.objectImageView);
        TextView coordinatesTextView = findViewById(R.id.coordinatesTextView);

        // Получаем данные из интента
        String title = getIntent().getStringExtra("title");
        String workingHours = getIntent().getStringExtra("workingHours");
        String details = getIntent().getStringExtra("details");
        int imageResourceId = getIntent().getIntExtra("imageResourceId", R.drawable.sophisky);
        double latitude = getIntent().getDoubleExtra("latitude", 0.0); // Получаем широту
        double longitude = getIntent().getDoubleExtra("longitude", 0.0);

        // Устанавливаем данные в элементы интерфейса
        titleTextView.setText(title);
        workingHoursTextView.setText(workingHours);
        detailsTextView.setText(details);
        objectImageView.setImageResource(imageResourceId);
        coordinatesTextView.setText("Координаты: " + latitude + ", " + longitude);

        // Обработчики нажатий на кнопки
        Button buttonAddReview = findViewById(R.id.buttonAddReview);
        buttonAddReview.setOnClickListener(v -> {
            Intent intent = new Intent(ObjectInfoActivity.this, ReviewActivity.class);
            intent.putExtra("objectId", title); // Используем title как идентификатор объекта
            startActivity(intent);
        });

        Button buttonFavorites = findViewById(R.id.buttonFavorites);
        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng position = new LatLng(latitude, longitude); // Создаем LatLng
                saveToFavorites(title, workingHours, details, imageResourceId, position);
            }
        });


        Button buttonViewReviews = findViewById(R.id.buttonViewReviews);
        buttonViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(ObjectInfoActivity.this, FavoriteReviewsActivity.class);
            intent.putExtra("objectId", title); // Передаём идентификатор объекта
            startActivity(intent);
        });

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрывает текущее активити и возвращает на предыдущий экран
            }
        });


        Button buttonShowMap = findViewById(R.id.buttonShowMap);
        buttonShowMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(ObjectInfoActivity.this, MapsActivity.class);
            mapIntent.putExtra("latitude", latitude);   // Передаем широту
            mapIntent.putExtra("longitude", longitude); // Передаем долготу
            startActivity(mapIntent);
        });

    }

    private void saveToFavorites(String title, String workingHours, String details, int imageResourceId, LatLng position) {
        // Создаем объект ObjectInfo с координатами, используя новое поле position
        ObjectInfo favoritePlace = new ObjectInfo(title, workingHours, details, imageResourceId, position, null);

        // Проверяем, уже существует ли место в избранных
        if (!FavoritePlacesManager.getInstance().isPlaceFavorite(favoritePlace)) {
            FavoritePlacesManager.getInstance().addPlace(favoritePlace);
            Toast.makeText(this, title + " добавлено в избранные места!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, title + " уже добавлено в избранные места!", Toast.LENGTH_SHORT).show();
        }
    }


}
