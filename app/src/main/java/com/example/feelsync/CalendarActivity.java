package com.example.feelsync;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    Calendar calendar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calenderView);
        calendar = Calendar.getInstance();

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("dates");

        setDate(1, 1, 2025);
        getDate();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Display selected date
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(CalendarActivity.this, selectedDate, Toast.LENGTH_SHORT).show();

                // Save the selected date to Firebase
                saveDateToFirebase(selectedDate);
            }
        });
    }

    public void getDate() {
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(date);
        String selected_data = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(this, selected_data, Toast.LENGTH_SHORT).show();
    }

    public void setDate(int day, int month, int year) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);
    }

    // Method to save selected date to Firebase
    private void saveDateToFirebase(String date) {
        String id = databaseReference.push().getKey(); // Generate a unique key for each entry
        if (id != null) {
            // Save date under the unique key
            databaseReference.child(id).setValue(date).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(CalendarActivity.this, "Date saved to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CalendarActivity.this, "Failed to save date", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}