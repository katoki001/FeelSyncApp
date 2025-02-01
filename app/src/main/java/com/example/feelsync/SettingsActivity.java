package com.example.feelsync;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

public class SettingsActivity extends MainPageActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button settingsButton = findViewById(R.id.settingsBtn);

        if (settingsButton != null) {
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Settings button clicked!");
                }
            });
        } else {
            Log.e(TAG, "Settings button not found!");
        }
    }
}
