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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
=======
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
>>>>>>> 759d1d9 (changes in code)

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
<<<<<<< HEAD
    private CustomCalendarView customCalendarView;
    private FirebaseFirestore db;
    private CollectionReference notesRef;
=======
    private CalendarView calendarView;
    private Calendar calendar;
<<<<<<< HEAD
    private FirebaseFirestore db;
    private CollectionReference notesRef;

=======
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
>>>>>>> 759d1d9 (changes in code)
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
    private Button addNoteButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notesList;
    private String selectedDate;
    private static final int ADD_NOTE_REQUEST = 1;
<<<<<<< HEAD
    private BottomNavigationView bottomNavigationView;
=======
<<<<<<< HEAD

    private BottomNavigationView bottomNavigationView;
=======
>>>>>>> 759d1d9 (changes in code)
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

<<<<<<< HEAD
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
=======
<<<<<<< HEAD
        // Инициализация Views
        calendarView = findViewById(R.id.calenderView);
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        calendar = Calendar.getInstance();

        // Инициализация Firebase
        db = FirebaseFirestore.getInstance();
        notesRef = db.collection("notes");

        // Настройка RecyclerView
=======
        // Initialize Views
        calendarView = findViewById(R.id.calenderView);
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recyclerView);
        calendar = Calendar.getInstance();

        // Initialize Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("notes");

        // Setup RecyclerView
>>>>>>> 759d1d9 (changes in code)
        notesList = new ArrayList<>();
        noteAdapter = new NoteAdapter(notesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);

<<<<<<< HEAD
        // Карта для хранения цветов дат
        dateColorMap = new HashMap<>();

        // Получение текущей даты
        selectedDate = getCurrentDate();

        // Загрузка заметок
        loadNotesFromFirebase();

        // Открытие AddNoteActivity
        addNoteButton.setOnClickListener(v -> openAddNoteActivity());

        // Изменение выбранной даты
=======
        // Map to store colors for dates
        dateColorMap = new HashMap<>();

        // Get current date
        selectedDate = getCurrentDate();

        // Load notes from Firebase
        loadNotesFromFirebase();

        // Open AddNoteActivity with selected date
        addNoteButton.setOnClickListener(v -> openAddNoteActivity());

        // Change selected date and load notes for that date
>>>>>>> 759d1d9 (changes in code)
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            loadNotesFromFirebase();
        });
<<<<<<< HEAD

        // Обработчик для Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_calendar) {
                    return true;
                } else if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_ai) {
                    startActivity(new Intent(getApplicationContext(), AIChatActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_music) {
                    startActivity(new Intent(getApplicationContext(), MusicActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        } else {
            Log.e("CalendarActivity", "BottomNavigationView is null. Check XML layout.");
        }

    }

    private void openAddNoteActivity() {
        Intent intent = new Intent(CalendarActivity.this, AddNoteActivity.class);
        intent.putExtra("SELECTED_DATE", selectedDate);
        startActivityForResult(intent, ADD_NOTE_REQUEST);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void loadNotesFromFirebase() {
        notesRef.whereEqualTo("date", selectedDate).get()
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
                        // Здесь можно реализовать отображение эмоций в календаре
                    } else {
                        Log.e("CalendarActivity", "Ошибка загрузки данных", task.getException());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            loadNotesFromFirebase();
        }
    }
=======
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
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

<<<<<<< HEAD
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
=======
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
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
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
<<<<<<< HEAD

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == ADD_NOTE_REQUEST) {
            // Only handle new note addition
            loadNotesFromFirebase();
        }
    }
}
=======
>>>>>>> 759d1d9 (changes in code)
}
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
