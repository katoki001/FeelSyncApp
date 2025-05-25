package com.example.feelsync;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
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
    private FirebaseAuth mAuth;
    private String userId;
    private boolean isEditMode = false;
    private long existingTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = mAuth.getCurrentUser().getUid();

        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
        noteInput = findViewById(R.id.note_input);
        saveButton = findViewById(R.id.save_button);

        refreshColorViews();

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("SELECTED_DATE");
        isEditMode = intent.getBooleanExtra("EDIT_NOTE", false);

        if (isEditMode) {
            saveButton.setText("Update Note");
            String noteText = intent.getStringExtra("NOTE_TEXT");
            String emotion = intent.getStringExtra("NOTE_EMOTION");
            existingTimestamp = intent.getLongExtra("NOTE_TIMESTAMP", 0);

            noteInput.setText(noteText);
            selectColor(emotion); // This will also refresh views
        } else {
            saveButton.setText("Save Note");
            selectedEmotion = "Happy";
            int defaultColor = getCustomColor("Happy");
            selectedRed = Color.red(defaultColor);
            selectedGreen = Color.green(defaultColor);
            selectedBlue = Color.blue(defaultColor);
        }

        colorHappy.setOnClickListener(v -> selectColor("Happy"));
        colorAngry.setOnClickListener(v -> selectColor("Angry"));
        colorSad.setOnClickListener(v -> selectColor("Sad"));
        colorCalm.setOnClickListener(v -> selectColor("Calm"));
        colorLove.setOnClickListener(v -> selectColor("Lovely"));

        saveButton.setOnClickListener(v -> saveData());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            refreshColorViews(); // Refresh button colors after returning from ChooseColorActivity
        }
    }

    private int getCustomColor(String emotion) {
        SharedPreferences sharedPref = getSharedPreferences("ColorPreferences", Context.MODE_PRIVATE);
        String colorKey = "";

        switch (emotion) {
            case "Happy": colorKey = "colorHappy"; break;
            case "Angry": colorKey = "colorAngry"; break;
            case "Sad": colorKey = "colorSad"; break;
            case "Calm": colorKey = "colorCalm"; break;
            case "Lovely": colorKey = "colorLove"; break;
        }

        if (colorKey.isEmpty()) return Color.WHITE;

        int defaultColor;
        switch (colorKey) {
            case "colorHappy": defaultColor = Color.YELLOW; break;
            case "colorAngry": defaultColor = Color.RED; break;
            case "colorSad": defaultColor = Color.BLUE; break;
            case "colorCalm": defaultColor = Color.GREEN; break;
            case "colorLove": defaultColor = Color.MAGENTA; break;
            default: defaultColor = Color.WHITE;
        }

        int red = sharedPref.getInt(colorKey + "_red", Color.red(defaultColor));
        int green = sharedPref.getInt(colorKey + "_green", Color.green(defaultColor));
        int blue = sharedPref.getInt(colorKey + "_blue", Color.blue(defaultColor));

        return Color.rgb(red, green, blue);
    }

    private void selectColor(String emotion) {
        this.selectedEmotion = emotion;

        int customColor = getCustomColor(emotion);
        selectedRed = Color.red(customColor);
        selectedGreen = Color.green(customColor);
        selectedBlue = Color.blue(customColor);

        refreshColorViews(); // Update UI with latest colors

        Toast.makeText(this, "Selected: " + emotion, Toast.LENGTH_SHORT).show();
    }

    private void refreshColorViews() {
        colorHappy.setBackgroundColor(getCustomColor("Happy"));
        colorAngry.setBackgroundColor(getCustomColor("Angry"));
        colorSad.setBackgroundColor(getCustomColor("Sad"));
        colorCalm.setBackgroundColor(getCustomColor("Calm"));
        colorLove.setBackgroundColor(getCustomColor("Lovely"));

        if (selectedEmotion != null) {
            View selectedView = null;
            switch (selectedEmotion) {
                case "Happy": selectedView = colorHappy; break;
                case "Angry": selectedView = colorAngry; break;
                case "Sad": selectedView = colorSad; break;
                case "Calm": selectedView = colorCalm; break;
                case "Lovely": selectedView = colorLove; break;
            }

            if (selectedView != null) {
                selectedView.setBackgroundColor(getCustomColor(selectedEmotion));
            }
        }
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

        db.collection("notes").document(userId).collection("userNotes")
                .add(noteData)
                .addOnSuccessListener(documentReference -> {
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
                    Log.e("AddNoteActivity", "Error saving note", e);
                });
    }

    private void updateExistingNote(String note) {
        db.collection("notes").document(userId).collection("userNotes")
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
}