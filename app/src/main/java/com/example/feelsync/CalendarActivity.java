package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    private CustomCalendarView customCalendarView;
    private FirebaseFirestore db;
    private CollectionReference notesRef;
    private Button addNoteButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notesList;
    private String selectedDate;
    private static final int ADD_NOTE_REQUEST = 1;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initViews();
        initFirebase();
        setupRecyclerView();
        loadNotesFromFirebase();

        customCalendarView.setOnDateSelectedListener(date -> {
            selectedDate = date;
            loadNotesFromFirebase();
        });

        addNoteButton.setOnClickListener(v -> openAddNoteActivity());
        setupBottomNavigation();
    }

    private void initViews() {
        customCalendarView = findViewById(R.id.customCalendarView);
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        notesList = new ArrayList<>();
        selectedDate = getCurrentDate();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        notesRef = db.collection("notes");
    }

    private void setupRecyclerView() {
        noteAdapter = new NoteAdapter(notesList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
    }

    public void deleteNote(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove from local list immediately
                    int position = notesList.indexOf(note);
                    if (position != -1) {
                        notesList.remove(position);
                        noteAdapter.notifyItemRemoved(position);
                    }

                    // Delete from Firestore
                    deleteFromFirestore(note, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFromFirestore(Note note, int originalPosition) {
        notesRef.whereEqualTo("timestamp", note.timestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnFailureListener(e -> {
                                        // Re-add if deletion fails
                                        if (originalPosition != -1) {
                                            notesList.add(originalPosition, note);
                                            noteAdapter.notifyItemInserted(originalPosition);
                                        }
                                        Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Re-add if query fails
                        if (originalPosition != -1) {
                            notesList.add(originalPosition, note);
                            noteAdapter.notifyItemInserted(originalPosition);
                        }
                        Toast.makeText(this, "Error finding note to delete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openAddNoteActivity() {
        Intent intent = new Intent(CalendarActivity.this, AddNoteActivity.class);
        intent.putExtra("SELECTED_DATE", selectedDate);
        startActivityForResult(intent, ADD_NOTE_REQUEST);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String formatDate(int day, int month, int year) {
        return String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year);
    }

    private void loadNotesFromFirebase() {
        // Load ALL notes, not just for selected date
        notesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesList.clear();
                        Map<String, List<String>> dateEmotionsMap = new HashMap<>();
                        Map<String, List<Note>> allNotesMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Note note = document.toObject(Note.class);
                                note.setFirestoreTimestamp(document.getLong("timestamp"));

                                // Skip if required fields are null
                                if (note.date == null || note.emotion == null || note.note == null) {
                                    continue;
                                }

                                // Store all notes by date
                                allNotesMap.computeIfAbsent(note.date, k -> new ArrayList<>()).add(note);

                                // Store emotions by date
                                dateEmotionsMap.computeIfAbsent(note.date, k -> new ArrayList<>()).add(note.emotion);
                            } catch (Exception e) {
                                Log.e("CalendarActivity", "Error processing document", e);
                            }
                        }

                        // Update notes for currently selected date
                        if (allNotesMap.containsKey(selectedDate)) {
                            notesList.addAll(allNotesMap.get(selectedDate));
                        }

                        noteAdapter.notifyDataSetChanged();
                        updateCalendarView(dateEmotionsMap);
                    } else {
                        Log.e("CalendarActivity", "Error loading data", task.getException());
                    }
                });
    }

    private void updateCalendarView(Map<String, List<String>> dateEmotionsMap) {
        customCalendarView.setDateEmotionsMap(new HashMap<>(dateEmotionsMap)); // Create new instance
        customCalendarView.setSelectedDate(selectedDate);
        customCalendarView.invalidate(); // Force redraw
    }
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_calendar) {
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(getApplicationContext(), AIChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_music) {
                startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == ADD_NOTE_REQUEST) {
            // Only handle new note addition
            loadNotesFromFirebase();
        }
    }
}