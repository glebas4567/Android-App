package com.example.googlemapsjava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверяем, авторизован ли пользователь
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // Если пользователь уже вошел, сразу переходим в CatalogActivity
            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
            finish();
            return; // Прекращаем выполнение onCreate для LoginActivity
        }

        // Если пользователь не авторизован, загружаем экран авторизации
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Пожалуйста, введите имя пользователя и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUserValid = databaseHelper.checkUsernamePassword(username, password);
        if (isUserValid) {
            Toast.makeText(this, "Авторизация успешна!", Toast.LENGTH_SHORT).show();

            // Сохраняем имя пользователя и состояние авторизации
            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Неправильное имя пользователя или пароль", Toast.LENGTH_SHORT).show();
        }
    }
}
