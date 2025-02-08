package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private Calendar calendar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button addNoteButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notesList;
    private Map<String, String> dateColorMap;
    private String selectedDate;
    private static final int ADD_NOTE_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize Views
        calendarView = findViewById(R.id.calenderView);
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recyclerView);
        calendar = Calendar.getInstance();

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("notes");

        // Setup RecyclerView
        notesList = new ArrayList<>();
        noteAdapter = new NoteAdapter(notesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

        // Map to store colors for dates
        dateColorMap = new HashMap<>();

        // Get current date
        selectedDate = getCurrentDate();

        // Load notes from Firebase
        loadNotesFromFirebase();

        // Open AddNoteActivity with selected date
        addNoteButton.setOnClickListener(v -> openAddNoteActivity());

        // Change selected date and load notes for that date
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            loadNotesFromFirebase();
        });
    }

    private void openAddNoteActivity() {
        Intent intent = new Intent(CalendarActivity.this, AddNoteActivity.class);
        intent.putExtra("SELECTED_DATE", selectedDate);
        startActivityForResult(intent, ADD_NOTE_REQUEST);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendarView.getDate());
    }

    // Load notes from Firebase
    private void loadNotesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notesList.clear();
                dateColorMap.clear();

                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null) {
                        notesList.add(note);
                        dateColorMap.put(note.date, note.emotion);
                    }
                }
                noteAdapter.notifyDataSetChanged();
                updateCalendarColors();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CalendarActivity", "Failed to load notes", error.toException());
            }
        });
    }

    // Change calendar day colors based on selected emotion
    private void updateCalendarColors() {
        for (Map.Entry<String, String> entry : dateColorMap.entrySet()) {
            String color = entry.getValue();
            if (color.contains("Red")) {
                calendarView.setBackgroundColor(Color.RED);
            } else if (color.contains("Blue")) {
                calendarView.setBackgroundColor(Color.BLUE);
            } else if (color.contains("Green")) {
                calendarView.setBackgroundColor(Color.GREEN);
            }
        }
    }
}
