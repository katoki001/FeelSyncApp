package com.example.feelsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private EditText noteInput;
    private Button saveButton;
    private int selectedRed, selectedGreen, selectedBlue;
    private String selectedDate, selectedEmotion;
    private FirebaseFirestore db;
    private boolean isEditMode = false;
    private long existingTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
        noteInput = findViewById(R.id.note_input);
        saveButton = findViewById(R.id.save_button);

        // Get data from Intent
        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("SELECTED_DATE");
        isEditMode = intent.getBooleanExtra("EDIT_NOTE", false);

        if (isEditMode) {
            // Edit existing note
            saveButton.setText("Update Note");
            String noteText = intent.getStringExtra("NOTE_TEXT");
            String emotion = intent.getStringExtra("NOTE_EMOTION");
            existingTimestamp = intent.getLongExtra("NOTE_TIMESTAMP", 0);

            noteInput.setText(noteText);
            selectColor(emotion); // This will set the appropriate color
        } else {
            // New note
            saveButton.setText("Save Note");
            selectedRed = 255;
            selectedGreen = 255;
            selectedBlue = 255;
        }

        // Set click listeners
        colorHappy.setOnClickListener(v -> selectColor("Happy"));
        colorAngry.setOnClickListener(v -> selectColor("Angry"));
        colorSad.setOnClickListener(v -> selectColor("Sad"));
        colorCalm.setOnClickListener(v -> selectColor("Calm"));
        colorLove.setOnClickListener(v -> selectColor("Lovely"));
        saveButton.setOnClickListener(v -> saveData());

        // Load saved colors
        loadSavedColors();
    }

    private void selectColor(String emotion) {
        this.selectedEmotion = emotion;
        switch (emotion) {
            case "Happy":
                selectedRed = 255;
                selectedGreen = 223;
                selectedBlue = 85;
                break;
            case "Angry":
                selectedRed = 255;
                selectedGreen = 0;
                selectedBlue = 0;
                break;
            case "Sad":
                selectedRed = 0;
                selectedGreen = 0;
                selectedBlue = 255;
                break;
            case "Calm":
                selectedRed = 0;
                selectedGreen = 255;
                selectedBlue = 0;
                break;
            case "Lovely":
                selectedRed = 255;
                selectedGreen = 105;
                selectedBlue = 180;
                break;
        }

        // Reset all colors to default
        colorHappy.setBackgroundColor(getSavedColor(getSharedPreferences("ColorPreferences", MODE_PRIVATE), "colorHappy"));
        colorAngry.setBackgroundColor(getSavedColor(getSharedPreferences("ColorPreferences", MODE_PRIVATE), "colorAngry"));
        colorSad.setBackgroundColor(getSavedColor(getSharedPreferences("ColorPreferences", MODE_PRIVATE), "colorSad"));
        colorCalm.setBackgroundColor(getSavedColor(getSharedPreferences("ColorPreferences", MODE_PRIVATE), "colorCalm"));
        colorLove.setBackgroundColor(getSavedColor(getSharedPreferences("ColorPreferences", MODE_PRIVATE), "colorLove"));

        // Highlight selected color
        View selectedView = null;
        switch (emotion) {
            case "Happy": selectedView = colorHappy; break;
            case "Angry": selectedView = colorAngry; break;
            case "Sad": selectedView = colorSad; break;
            case "Calm": selectedView = colorCalm; break;
            case "Lovely": selectedView = colorLove; break;
        }

        if (selectedView != null) {
            selectedView.setBackgroundColor(Color.rgb(selectedRed, selectedGreen, selectedBlue));
        }

        Toast.makeText(this, "Selected: " + emotion, Toast.LENGTH_SHORT).show();
    }

    private void saveData() {
        String note = noteInput.getText().toString().trim();

        if (note.isEmpty()) {
            Toast.makeText(this, "Please write a note", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedEmotion == null) {
            Toast.makeText(this, "Please select an emotion", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            updateExistingNote(note);
        } else {
            saveNewNote(note);
        }
    }

    private void saveNewNote(String note) {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("date", selectedDate);
        noteData.put("emotion", selectedEmotion);
        noteData.put("note", note);
        noteData.put("red", selectedRed);
        noteData.put("green", selectedGreen);
        noteData.put("blue", selectedBlue);
        noteData.put("timestamp", timestamp);

        db.collection("notes").add(noteData)
                .addOnSuccessListener(documentReference -> {
                    setResult(RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
                    Log.e("AddNoteActivity", "Error saving note", e);
                });


    }

    private void updateExistingNote(String note) {
        db.collection("notes")
                .whereEqualTo("timestamp", existingTimestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("date", selectedDate);
                            updates.put("emotion", selectedEmotion);
                            updates.put("note", note);
                            updates.put("red", selectedRed);
                            updates.put("green", selectedGreen);
                            updates.put("blue", selectedBlue);

                            document.getReference().update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                setResult(RESULT_OK);
                                finish();
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
                            })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show();
                                        Log.e("AddNoteActivity", "Error updating note", e);
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Error finding note to update", Toast.LENGTH_SHORT).show();
                        Log.e("AddNoteActivity", "Error finding note", task.getException());
                    }
                });
    }

    private void loadSavedColors() {
        SharedPreferences sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE);
        colorHappy.setBackgroundColor(getSavedColor(sharedPreferences, "colorHappy"));
        colorAngry.setBackgroundColor(getSavedColor(sharedPreferences, "colorAngry"));
        colorSad.setBackgroundColor(getSavedColor(sharedPreferences, "colorSad"));
        colorCalm.setBackgroundColor(getSavedColor(sharedPreferences, "colorCalm"));
        colorLove.setBackgroundColor(getSavedColor(sharedPreferences, "colorLove"));
    }

    private int getSavedColor(SharedPreferences sharedPreferences, String colorKey) {
        int defaultColor;
        switch (colorKey) {
            case "colorHappy": defaultColor = Color.YELLOW; break;
            case "colorAngry": defaultColor = Color.RED; break;
            case "colorSad": defaultColor = Color.BLUE; break;
            case "colorCalm": defaultColor = Color.GREEN; break;
            case "colorLove": defaultColor = Color.MAGENTA; break;
            default: defaultColor = Color.WHITE;
        }

        int red = sharedPreferences.getInt(colorKey + "_red", Color.red(defaultColor));
        int green = sharedPreferences.getInt(colorKey + "_green", Color.green(defaultColor));
        int blue = sharedPreferences.getInt(colorKey + "_blue", Color.blue(defaultColor));

        return Color.rgb(red, green, blue);
    }
}