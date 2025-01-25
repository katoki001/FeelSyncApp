package com.example.feelsync;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;


public class MainPageActivity extends AppCompatActivity {
        private Button instructionBtn, settingsBtn, aiBtn, calendarBtn, musicBtn, triggerBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mainpage); // Ensure the XML file is correctly linked

            // Initialize buttons
            instructionBtn = findViewById(R.id.Instructionbtn);
            settingsBtn = findViewById(R.id.Setingsbtn);
            aiBtn = findViewById(R.id.Aibtn);
            calendarBtn = findViewById(R.id.Calendarbtn);
            musicBtn = findViewById(R.id.Musicbtn);
            triggerBtn = findViewById(R.id.Trigerbtn);

            // Set click listeners for buttons
            /*instructionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Instruction button clicked.");
                    // Navigate to the Instruction Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
                    startActivity(intent);
                }
            });

            settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Settings button clicked.");
                    // Navigate to the Settings Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });

            aiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "AI Chat button clicked.");
                    // Navigate to the AI Chat Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
                    startActivity(intent);
                }
            });*/

            calendarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Calendar button clicked.");
                    // Navigate to the Calendar Activity or perform related action
                    Intent intent = new Intent(MainPageActivity.this, CalendarActivity.class);
                    startActivity(intent);
                }
            });

            /*musicBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Music button clicked.");
                    // Navigate to the Music Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, MusicActivity.class);
                    startActivity(intent);
                }
            });

            triggerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Triggers button clicked.");
                    // Navigate to the Triggers Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, TriggerActivity.class);
                    startActivity(intent);
                }
            });*/
        }
    }


