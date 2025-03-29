package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private ImageView nightMode, notification, privateAccount, chooseColorArrow;
    private SwitchCompat nightModeSwitch, notificationSwitch, privateAccountSwitch;
    private AppCompatButton editProfileButton;
    private TextView userName;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        nightMode = findViewById(R.id.night_mode);
        notification = findViewById(R.id.notification);
        privateAccount = findViewById(R.id.Private);
        editProfileButton = findViewById(R.id.edit_profile);
        userName = findViewById(R.id.user_name);
        chooseColorArrow = findViewById(R.id.choose_color_arrow); // Найти стрелку

        // Set up button click listener
        editProfileButton.setOnClickListener(v -> editProfile());

        // Открытие выбора цвета при нажатии на стрелку
        chooseColorArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChooseColorActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation Setup
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_settings) {
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(getApplicationContext(), AIChatActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_music) {
                startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void editProfile() {
        // Здесь логика редактирования профиля
    }
}

