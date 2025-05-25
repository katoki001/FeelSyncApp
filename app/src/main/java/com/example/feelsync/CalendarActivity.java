package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
    private static final String NOTES_COLLECTION = "notes";
    private static final String USER_NOTES_SUBCOLLECTION = "userNotes";

    private CustomCalendarView customCalendarView;
    private FirebaseFirestore db;
    private CollectionReference notesRef;
    private Button addNoteButton;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private ArrayList<Note> notesList;
    private String selectedDate;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private String userId;
    private Map<String, List<String>> allDateEmotionsMap = new HashMap<>();

    // Views for expand/collapse functionality
    private LinearLayout emotionHeader;
    private LinearLayout emotionContent;
    private ImageView arrowIcon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = mAuth.getCurrentUser().getUid();

        initViews();
        initFirebase();
        setupRecyclerView();

        // Load all emotion data first
        fetchAllEmotionDataForCalendar();

        // Then load notes for selected date
        loadNotesForSelectedDate(selectedDate);

        customCalendarView.setOnDateSelectedListener(date -> {
            selectedDate = date;
            loadNotesForSelectedDate(selectedDate);
        });

        addNoteButton.setOnClickListener(v -> openAddNoteActivity());

        setupBottomNavigation();

        // Set up expand/collapse functionality for "Your Emotions" section
        setupEmotionSection();
    }

    private void initViews() {
        customCalendarView = findViewById(R.id.customCalendarView);
        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        notesList = new ArrayList<>();
        selectedDate = getCurrentDate();

        // Views for expand/collapse functionality
        emotionHeader = findViewById(R.id.emotion_header);
        emotionContent = findViewById(R.id.emotion_content);
        arrowIcon = findViewById(R.id.arrow_icon);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        notesRef = db.collection(NOTES_COLLECTION).document(userId).collection(USER_NOTES_SUBCOLLECTION);
    }

    private void setupRecyclerView() {
        noteAdapter = new NoteAdapter(notesList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
    }

    private void fetchAllEmotionDataForCalendar() {
        notesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allDateEmotionsMap.clear();
                        Map<String, Integer> emotionCounts = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Note note = document.toObject(Note.class);
                                String date = note.getDate();
                                String emotion = note.getEmotion();

                                // Update emotion counts for chart
                                emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);

                                // Update date-emotions mapping for calendar
                                if (!allDateEmotionsMap.containsKey(date)) {
                                    allDateEmotionsMap.put(date, new ArrayList<>());
                                }
                                allDateEmotionsMap.get(date).add(emotion);
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing document", e);
                            }
                        }

                        // Update both calendar and chart
                        updateCalendarView(allDateEmotionsMap);
                        updateEmotionChart(emotionCounts);
                    } else {
                        Toast.makeText(this, "Error loading emotions", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading emotions", task.getException());
                    }
                });
    }

    @Override
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
                    notesRef.document(note.getDocumentId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Note deleted from Firestore");
                                // Refresh both calendar and notes list
                                fetchAllEmotionDataForCalendar();
                                loadNotesForSelectedDate(selectedDate);
                            })
                            .addOnFailureListener(e -> {
                                if (position != -1) {
                                    notesList.add(position, note);
                                    noteAdapter.notifyItemInserted(position);
                                }
                                Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error deleting note", e);
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void editNote(Note note) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("EDIT_NOTE", true);
        intent.putExtra("NOTE_DATE", note.getDate());
        intent.putExtra("NOTE_EMOTION", note.getEmotion());
        intent.putExtra("NOTE_TEXT", note.getNote());
        intent.putExtra("NOTE_TIMESTAMP", note.getTimestamp());
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

    @SuppressLint("NotifyDataSetChanged")
    private void loadNotesForSelectedDate(String selectedDate) {
        notesRef.whereEqualTo("date", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Note note = document.toObject(Note.class);
                                note.setDocumentId(document.getId());
                                Long timestamp = document.getLong("timestamp");
                                note.setTimestamp(timestamp != null ? timestamp : System.currentTimeMillis());
                                if (note.getDate() == null || note.getEmotion() == null || note.getNote() == null) {
                                    Log.w(TAG, "Skipping incomplete note: " + document.getId());
                                    continue;
                                }
                                notesList.add(note);
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing document: " + document.getId(), e);
                            }
                        }
                        noteAdapter.notifyDataSetChanged();
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

    private void updateEmotionChart(Map<String, Integer> emotionCounts) {
        EmotionChartView emotionChartView = findViewById(R.id.monthly_chart_view);
        if (emotionChartView != null) {
            emotionChartView.setEmotionData(emotionCounts);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_calendar) {
                return true;
            }
            bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainPageActivity.class));
            } else if (itemId == R.id.nav_ai) {
                startActivity(new Intent(this, AIChatActivity.class));
            } else if (itemId == R.id.nav_music) {
                startActivity(new Intent(this, MusicActivity.class));
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
            bottomNavigationView.postDelayed(() ->
                    bottomNavigationView.getMenu().setGroupCheckable(0, true, true), 300);
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Refresh both calendar and notes list
            fetchAllEmotionDataForCalendar();
            loadNotesForSelectedDate(selectedDate);
        }
    }

    private void setupEmotionSection() {
        emotionHeader.setOnClickListener(v -> {
            if (emotionContent.getVisibility() == View.VISIBLE) {
                // Collapse the content
                emotionContent.setVisibility(View.GONE);
                arrowIcon.setImageResource(R.drawable.arrow_up); // Change arrow to point up
            } else {
                // Expand the content
                emotionContent.setVisibility(View.VISIBLE);
                arrowIcon.setImageResource(R.drawable.arrow_down); // Change arrow to point down
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}