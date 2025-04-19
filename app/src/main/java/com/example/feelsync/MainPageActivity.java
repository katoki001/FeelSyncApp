package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPageActivity extends AppCompatActivity {
    private static final String TAG = "MainPageActivity";
    private static final String EXTRA_SELECTED_DATE = "SELECTED_DATE";

    private BottomNavigationView bottomNavigationView;
    private ImageButton settingsButton;
    private String selectedDate;

    private final ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle settings result if needed
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        initializeViews();
        setupBottomNavigation();
        setupSettingsButton();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        settingsButton = findViewById(R.id.settingsbtn);
        // Initialize selectedDate with your date value
        selectedDate = ""; // Set your initial date value here
    }

    private void setupSettingsButton() {
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(EXTRA_SELECTED_DATE, selectedDate);
            settingsLauncher.launch(intent);
        });
    }

    private void setupBottomNavigation() {
        // Set the correct selected item for MainPageActivity
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // If already on current activity, do nothing
            if (id == R.id.nav_home) {
                return true;
            }

            Class<?> targetActivity = null;
            if (id == R.id.nav_calendar) {
                targetActivity = CalendarActivity.class;
            } else if (id == R.id.nav_ai) {
                targetActivity = AIChatActivity.class;
            } else if (id == R.id.nav_statitstics) {
                targetActivity = StatisticsActivity.class;
            } else if (id == R.id.nav_music) {
                targetActivity = MusicActivity.class;
            }

            if (targetActivity != null) {
                navigateToActivity(targetActivity);
                return true;
            }
            return false;
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        // Clear back stack and prevent multiple instances
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}