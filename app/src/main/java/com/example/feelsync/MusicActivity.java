package com.example.feelsync;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MusicActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_music) {
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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
            }
            return false;
        });
    }
}