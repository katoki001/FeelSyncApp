package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feelsync.AIChatActivity;
import com.example.feelsync.CalendarActivity;
import com.example.feelsync.MusicActivity;
import com.example.feelsync.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.proglish2.R;

public class MainPageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize buttons
        bottomNavigationView = findViewById(R.id.bottom_navigation);

<<<<<<< HEAD
        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(getApplicationContext(), CalendarActivity.class)); //change to QuizActivity
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(getApplicationContext(), AIChatActivity.class)); //change to QuizActivity
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_music) {
                startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                //overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
=======
            // Set click listeners for buttons
            /*instructionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainActivity", "Instruction button clicked.");
                    // Navigate to the Instruction Activity or perform related action
                    Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
                    startActivity(intent);
                }
            });*/

            settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("MainPageActivity", "Settings button clicked.");
                    // Navigate to the Calendar Activity or perform related action
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
>>>>>>> 759d1d9 (changes in code)
    }
}