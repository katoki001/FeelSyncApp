package com.example.feelsync;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;

import com.example.proglish2.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageView nightMode, notification, privateAccount;
    private SwitchCompat nightModeSwitch, notificationSwitch, privateAccountSwitch;
    private AppCompatButton editProfileButton;
    private TextView userName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);




        // Initialize UI components
        nightMode = findViewById(R.id.night_mode);
        notification = findViewById(R.id.notification);
        privateAccount = findViewById(R.id.Private);

        /*nightModeSwitch = findViewById(R.id.night_mode);
        notificationSwitch = findViewById(R.id.notification);
        privateAccountSwitch = findViewById(R.id.Private);*/

        editProfileButton = findViewById(R.id.edit_profile);
        userName = findViewById(R.id.user_name);

        // Set up button click listener
        editProfileButton.setOnClickListener(v -> editProfile());

        // Toggle switches
        /*nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleNightMode(isChecked));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> toggleNotifications(isChecked));
        privateAccountSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> togglePrivateAccount(isChecked));*/
    }

    private void editProfile() {
        // Handle edit profile button click
    }

    private void toggleNightMode(boolean isEnabled) {
        // Handle night mode toggle
    }

    private void toggleNotifications(boolean isEnabled) {
        // Handle notification toggle
    }

    private void togglePrivateAccount(boolean isEnabled) {
        // Handle private account toggle
    }
}