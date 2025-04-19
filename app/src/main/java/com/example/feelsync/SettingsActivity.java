package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {
    private ImageView nightMode, notification_arrow, privateAccount, chooseColorArrow, logout_arrow;
    private SwitchCompat nightModeSwitch, notificationSwitch, privateAccountSwitch;
    private AppCompatButton editProfileButton;
    private TextView userName;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        nightMode = findViewById(R.id.night_mode);
        notification_arrow = findViewById(R.id.notification_arrow);
        logout_arrow = findViewById(R.id.logout_arrow);
        editProfileButton = findViewById(R.id.edit_profile);
        userName = findViewById(R.id.user_name);
        chooseColorArrow = findViewById(R.id.choose_color_arrow);

        chooseColorArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChooseColorActivity.class);
            startActivity(intent);
        });

        notification_arrow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, com.example.feelsync.NotificationActivity.class);
            startActivity(intent);
        });

        // Handle logout directly here
        logout_arrow.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(SettingsActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }


}