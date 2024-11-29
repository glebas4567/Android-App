package com.example.googlemapsjava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Получаем данные из SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", ""); // Получаем имя пользователя

        // Если имя пользователя пустое, значит, пользователь не авторизован, переходим на LoginActivity
        if (username.isEmpty()) {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Закрываем AccountActivity
            return; // Прекращаем выполнение этого метода
        }

        // Находим TextView и устанавливаем текст
        TextView usernameTextView = findViewById(R.id.textViewUsername);
        usernameTextView.setText("Имя пользователя: " + username);

        // Кнопки для отображения списков
        Button visitedPlacesButton = findViewById(R.id.buttonVisitedPlaces);
        Button favoritePlacesButton = findViewById(R.id.buttonFavoritePlaces);
        Button backButton = findViewById(R.id.buttonBack);
        Button logoutButton = findViewById(R.id.buttonLogout); // Кнопка выхода из аккаунта

        backButton.setOnClickListener(v -> {
            // Закрываем текущее активити и возвращаемся назад
            finish();
        });

        // Обработчик для кнопки посещенных мест
        visitedPlacesButton.setOnClickListener(v -> {
            Intent visitedPlacesIntent = new Intent(AccountActivity.this, VisitedPlacesActivity.class);
            ArrayList<ObjectInfo> visitedPlaces = VisitedPlacesManager.getInstance().getVisitedPlaces();
            visitedPlacesIntent.putParcelableArrayListExtra("visited_places", visitedPlaces);
            startActivity(visitedPlacesIntent);
        });

        // Обработчик для кнопки избранных мест
        favoritePlacesButton.setOnClickListener(v -> {
            Intent favoritePlacesIntent = new Intent(AccountActivity.this, FavoritePlacesActivity.class);
            ArrayList<ObjectInfo> favoritePlaces = FavoritePlacesManager.getInstance().getFavoritePlaces();
            favoritePlacesIntent.putParcelableArrayListExtra("favorite_places", favoritePlaces);
            startActivity(favoritePlacesIntent);
        });

        // Обработчик для кнопки выхода из аккаунта
        logoutButton.setOnClickListener(v -> {
            // Очищаем SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Удаляем все сохранённые данные
            editor.apply();

            // Возвращаемся на экран входа
            Intent logoutIntent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish(); // Завершаем текущую активность
        });
    }
}
