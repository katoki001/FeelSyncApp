package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class CalendarActivity extends AppCompatActivity implements NoteAdapter.NoteActionListener {
    private static final String TAG = "CalendarActivity";
    private static final int ADD_NOTE_REQUEST = 1;
    private CustomCalendarView customCalendarView;
    private FirebaseFirestore db;
    private CollectionReference notesRef;
    private Button addNoteButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notesList;
    private String selectedDate;
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
            fetchMonthlyEmotionData(date); // Fetch monthly emotion data
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
                    int position = notesList.indexOf(note);
                    if (position != -1) {
                        notesList.remove(position);
                        noteAdapter.notifyItemRemoved(position);
                    }
                    notesRef.whereEqualTo("timestamp", note.timestamp)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete()
                                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Note deleted from Firestore"))
                                                .addOnFailureListener(e -> {
                                                    if (position != -1) {
                                                        notesList.add(position, note);
                                                        noteAdapter.notifyItemInserted(position);
                                                    }
                                                    Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                                    Log.e(TAG, "Error deleting note", e);
                                                });
                                    }
                                } else {
                                    if (position != -1) {
                                        notesList.add(position, note);
                                        noteAdapter.notifyItemInserted(position);
                                    }
                                    Toast.makeText(this, "Error finding note to delete", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error finding note to delete", task.getException());
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void editNote(Note note) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("EDIT_NOTE", true);
        intent.putExtra("NOTE_DATE", note.date);
        intent.putExtra("NOTE_EMOTION", note.emotion);
        intent.putExtra("NOTE_TEXT", note.note);
        intent.putExtra("NOTE_TIMESTAMP", note.timestamp);
        startActivityForResult(intent, ADD_NOTE_REQUEST);
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

    private void loadNotesFromFirebase() {
        notesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesList.clear();
                        Map<String, List<String>> dateEmotionsMap = new HashMap<>();
                        Map<String, List<Note>> allNotesMap = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Note note = document.toObject(Note.class);
                                Long timestamp = document.getLong("timestamp");
                                note.timestamp = timestamp != null ? timestamp : System.currentTimeMillis();
                                if (note.date == null || note.emotion == null || note.note == null) {
                                    Log.w(TAG, "Skipping incomplete note");
                                    continue;
                                }
                                if (!allNotesMap.containsKey(note.date)) {
                                    allNotesMap.put(note.date, new ArrayList<>());
                                }
                                allNotesMap.get(note.date).add(note);
                                if (!dateEmotionsMap.containsKey(note.date)) {
                                    dateEmotionsMap.put(note.date, new ArrayList<>());
                                }
                                dateEmotionsMap.get(note.date).add(note.emotion);
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing document", e);
                            }
                        }
                        if (allNotesMap.containsKey(selectedDate)) {
                            notesList.addAll(allNotesMap.get(selectedDate));
                        }
                        noteAdapter.notifyDataSetChanged();
                        updateCalendarView(dateEmotionsMap);
                        Log.d(TAG, "Loaded " + notesList.size() + " notes for " + selectedDate);
                    } else {
                        Toast.makeText(this, "Error loading notes", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading notes", task.getException());
                    }
                });
    }

    private void updateCalendarView(Map<String, List<String>> dateEmotionsMap) {
        customCalendarView.setDateEmotionsMap(new HashMap<>(dateEmotionsMap));
        customCalendarView.setSelectedDate(selectedDate);
        customCalendarView.invalidate();
    }

    private void fetchMonthlyEmotionData(String selectedDate) {
        String[] dateParts = selectedDate.split("/");
        String yearMonth = dateParts[2] + "-" + dateParts[1];
        notesRef.whereGreaterThanOrEqualTo("date", yearMonth + "-01")
                .whereLessThan("date", yearMonth + "-32")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> emotionCounts = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Note note = document.toObject(Note.class);
                                String emotion = note.emotion;
                                emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing document", e);
                            }
                        }
                        updateEmotionChart(emotionCounts);
                    } else {
                        Toast.makeText(this, "Error loading emotions", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading emotions", task.getException());
                    }
                });
    }

    private void updateEmotionChart(Map<String, Integer> emotionCounts) {
        EmotionChartView emotionChartView = findViewById(R.id.monthly_chart_view);
        if (emotionChartView != null) {
            emotionChartView.setEmotionData(emotionCounts);
        }
    }

    private void setupBottomNavigation() {
        // Set the current item as selected (calendar for this activity)
        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // If already on current activity, do nothing
            if (itemId == R.id.nav_calendar) {
                return true;
            }

            // Temporarily disable menu items to prevent visual glitches
            bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            // Handle navigation
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainPageActivity.class));
            } else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(this, AIChatActivity.class));
            } else if (itemId == R.id.nav_music) {
                startActivity(new Intent(this, MusicActivity.class));
            } else if (itemId == R.id.nav_statitstics) {
                startActivity(new Intent(this, StatisticsActivity.class));
            }

            // Add smooth transition
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();

            // Re-enable menu items after a short delay
            new Handler().postDelayed(() ->
                    bottomNavigationView.getMenu().setGroupCheckable(0, true, true), 300);

            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            loadNotesFromFirebase();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}