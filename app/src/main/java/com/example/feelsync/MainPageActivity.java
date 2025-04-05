package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.feelsync.AIChatActivity;
import com.example.feelsync.CalendarActivity;
import com.example.feelsync.MusicActivity;
import com.example.feelsync.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

<<<<<<< HEAD
import java.text.SimpleDateFormat;
import java.util.*;

public class MainPageActivity extends AppCompatActivity {
    private static final String TAG = "MainPageActivity";
    private EmotionChartView monthlyChartView;
    private BottomNavigationView bottomNavigationView;
    private TextView monthTitle;
    private ImageButton prevMonthButton, nextMonthButton;
    private Calendar currentMonth;
    private FirebaseFirestore db;
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
=======
public class MainPageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

<<<<<<< HEAD
        initializeViews();
        setupFirebase();
        setupNavigation();
        loadInitialData();
    }

    private void initializeViews() {
        monthlyChartView = findViewById(R.id.monthly_chart_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        monthTitle = findViewById(R.id.month_title);
        prevMonthButton = findViewById(R.id.prevMonthButton);
        nextMonthButton = findViewById(R.id.nextMonthButton);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        currentMonth = Calendar.getInstance();
    }

    private void setupNavigation() {
        prevMonthButton.setOnClickListener(v -> navigateMonth(-1));
        nextMonthButton.setOnClickListener(v -> navigateMonth(1));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;

            Class<?> targetActivity = null;
            if (itemId == R.id.nav_calendar) targetActivity = CalendarActivity.class;
            else if (itemId == R.id.nav_settings) targetActivity = SettingsActivity.class;
            else if (itemId == R.id.nav_ai) targetActivity = AIChatActivity.class;
            else if (itemId == R.id.nav_music) targetActivity = MusicActivity.class;

            if (targetActivity != null) {
                startActivity(new Intent(this, targetActivity));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }
            return true;
        });
    }

    private void loadInitialData() {
        updateMonthTitle();
        loadEmotionData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Reloading emotion data");
        loadEmotionData();
    }

    private void navigateMonth(int monthsToAdd) {
        currentMonth.add(Calendar.MONTH, monthsToAdd);
        updateMonthTitle();
        loadEmotionData();
    }

    private void updateMonthTitle() {
        monthTitle.setText(monthFormat.format(currentMonth.getTime()));
    }

    private void loadEmotionData() {
        Calendar startCal = (Calendar) currentMonth.clone();
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        String startDate = dbDateFormat.format(startCal.getTime());
        String endDate = dbDateFormat.format(endCal.getTime());

        Log.d(TAG, "Loading data for: " + startDate + " to " + endDate);

        db.collection("notes")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> emotionCounts = initializeEmotionMap();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String emotion = document.getString("emotion");
                            if (emotion != null && emotionCounts.containsKey(emotion)) {
                                emotionCounts.put(emotion, emotionCounts.get(emotion) + 1);
                            }
                        }

                        Log.d(TAG, "Emotion counts: " + emotionCounts);
                        monthlyChartView.setEmotionData(emotionCounts);
                    } else {
                        Log.e(TAG, "Error loading data", task.getException());
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                        monthlyChartView.setEmotionData(initializeEmotionMap());
                    }
                });
    }

    private Map<String, Integer> initializeEmotionMap() {
        Map<String, Integer> map = new HashMap<>();
        for (String emotion : monthlyChartView.getEmotions()) {
            map.put(emotion, 0);
        }
        return map;
=======
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
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
    }
}