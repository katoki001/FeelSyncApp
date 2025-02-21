package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feelsync.AddNoteActivity;
import com.example.feelsync.Note;
import com.example.feelsync.NoteAdapter;
import com.example.proglish2.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private Calendar calendar;
    private FirebaseFirestore db;
    private CollectionReference notesRef;

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
        db = FirebaseFirestore.getInstance();
        notesRef = db.collection("notes");

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

    // Load notes from Firebase based on selected date
    private void loadNotesFromFirebase() {
        notesRef.whereEqualTo("date", selectedDate).get()  // Filter by selected date
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesList.clear();
                        dateColorMap.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Note note = document.toObject(Note.class);
                            notesList.add(note);
                            dateColorMap.put(note.date, note.emotion);
                        }
                        noteAdapter.notifyDataSetChanged();
                        updateCalendarColors();
                    } else {
                        Log.e("CalendarActivity", "Ошибка загрузки данных", task.getException());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Reload notes after returning from AddNoteActivity
            loadNotesFromFirebase();
        }
    }
}



