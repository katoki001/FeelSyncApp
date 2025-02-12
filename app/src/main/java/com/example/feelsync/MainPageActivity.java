package com.example.feelsync;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;


public class MainPageActivity extends AppCompatActivity {
    private Button SettingsBtn, aiBtn, calendarBtn, musicBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage); // Ensure the XML file is correctly linked

        // Initialize buttons

        SettingsBtn = findViewById(R.id.Settingsbtn);
        aiBtn = findViewById(R.id.Aibtn);
        calendarBtn = findViewById(R.id.Calendarbtn);
        musicBtn = findViewById(R.id.Musicbtn);

        // Set click listeners for buttons
        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainPageActivity", "Calendar button clicked.");
                // Navigate to the Calendar Activity or perform related action
                Intent intent = new Intent(MainPageActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });


            /*aiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "AI Chat button clicked.");
                    // Navigate to the AI Chat Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
                    startActivity(intent);
                }
            });*/




            /*musicBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Music button clicked.");
                    // Navigate to the Music Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                    startActivity(intent);
                }
            });*/
    }
}

