package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";
    private EmotionChartView monthlyChartView;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initializeViews();
        setupFirebase();
        loadEmotionData();
        setupBottomNavigation();
    }

    private void initializeViews() {
        monthlyChartView = findViewById(R.id.monthly_chart_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize with empty data
        Map<String, Integer> initialData = new HashMap<>();
        for (String emotion : monthlyChartView.getEmotions()) {
            initialData.put(emotion, 0);
        }
        monthlyChartView.setEmotionData(initialData);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_statitstics);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // If already on this activity, do nothing
            if (id == R.id.nav_statitstics) {
                return true;
            }

            Class<?> targetActivity = null;
            if (id == R.id.nav_calendar) {
                targetActivity = CalendarActivity.class;
            } else if (id == R.id.nav_home) {
                targetActivity = MainPageActivity.class;
            } else if (id == R.id.nav_ai) {
                targetActivity = AIChatActivity.class;
            } else if (id == R.id.nav_music) {
                targetActivity = MusicActivity.class;
            }

            if (targetActivity != null) {
                // Clear back stack and prevent multiple instances
                Intent intent = new Intent(this, targetActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadEmotionData() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        String monthStr = month < 10 ? "0" + month : String.valueOf(month);
        String yearMonthPrefix = year + "-" + monthStr;

        Log.d(TAG, "Querying for month: " + yearMonthPrefix);

        db.collection("notes")
                .whereGreaterThanOrEqualTo("date", yearMonthPrefix + "-01")
                .whereLessThan("date", yearMonthPrefix + "-32")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Found " + task.getResult().size() + " documents");

                        Map<String, Integer> emotionCounts = new HashMap<>();
                        // Initialize all emotions with 0 counts
                        for (String emotion : monthlyChartView.getEmotions()) {
                            emotionCounts.put(emotion, 0);
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String emotion = document.getString("emotion");
                            Log.d(TAG, "Document emotion: " + emotion);
                            if (emotion != null && emotionCounts.containsKey(emotion)) {
                                emotionCounts.put(emotion, emotionCounts.get(emotion) + 1);
                            }
                        }

                        Log.d(TAG, "Final counts: " + emotionCounts);
                        monthlyChartView.setEmotionData(emotionCounts);
                    } else {
                        Log.e(TAG, "Error loading data", task.getException());
                        Toast.makeText(this, "Failed to load emotion data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}