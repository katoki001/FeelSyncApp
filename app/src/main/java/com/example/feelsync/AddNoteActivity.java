package com.example.feelsync;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private EditText noteInput;
    private Button uploadButton, saveButton;
    private int selectedRed, selectedGreen, selectedBlue; // Store RGB values
    private static final int IMAGE_REQUEST_CODE = 1;
    private String selectedDate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        db = FirebaseFirestore.getInstance();

        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
        noteInput = findViewById(R.id.note_input);
        saveButton = findViewById(R.id.save_button);

        // Receive the RGB values from ChooseColorActivity
        Intent intent = getIntent();
        selectedRed = intent.getIntExtra("selected_red", 255); // Default to white if no color
        selectedGreen = intent.getIntExtra("selected_green", 255);
        selectedBlue = intent.getIntExtra("selected_blue", 255);

        // Set the color based on the received RGB values
        setColorToViews(selectedRed, selectedGreen, selectedBlue);

        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        // Color selection button handlers
        colorHappy.setOnClickListener(v -> selectColor("Happy"));
        colorAngry.setOnClickListener(v -> selectColor("Angry"));
        colorSad.setOnClickListener(v -> selectColor("Sad"));
        colorCalm.setOnClickListener(v -> selectColor("Calm"));
        colorLove.setOnClickListener(v -> selectColor("Lovely"));

        saveButton.setOnClickListener(v -> saveData());
    }

    private void selectColor(String color) {
        // This logic will remain as before, allowing the user to select a color name
        // if you want additional functionality for color names.
    }

    private void setColorToViews(int red, int green, int blue) {
        // Convert the RGB values to a color
        int color = Color.rgb(red, green, blue);

        // Update the color of the buttons/views
        colorHappy.setBackgroundColor(color);
        colorAngry.setBackgroundColor(color);
        colorSad.setBackgroundColor(color);
        colorCalm.setBackgroundColor(color);
        colorLove.setBackgroundColor(color);
    }

    private void saveData() {
        String note = noteInput.getText().toString().trim();

        if (note.isEmpty()) {
            Toast.makeText(this, "Please write a note.", Toast.LENGTH_SHORT).show();
        } else {
            saveNoteToFirestore(note);
        }
    }

    private void saveNoteToFirestore(String note) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("date", selectedDate);
        noteData.put("emotion", getColorNameFromRGB());  // You can store the color name or just RGB values
        noteData.put("note", note);
        noteData.put("red", selectedRed);   // Save the individual RGB values
        noteData.put("green", selectedGreen);
        noteData.put("blue", selectedBlue);

        // Save note to Firestore
        db.collection("notes").add(noteData)
                .addOnSuccessListener(documentReference -> {
                    String noteId = documentReference.getId(); // Get the Firestore document ID
                    Intent intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);
                    intent.putExtra("NOTE_TEXT", note);
                    intent.putExtra("NOTE_EMOTION", getColorNameFromRGB());  // Pass the emotion color
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show());
    }

    private String getColorNameFromRGB() {
        // Optionally, you can map the RGB values back to a color name, like "Happy", "Sad", etc.
        // This method can return the appropriate color name.
        if (selectedRed == 255 && selectedGreen == 0 && selectedBlue == 0) {
            return "Angry";
        } else if (selectedRed == 255 && selectedGreen == 255 && selectedBlue == 0) {
            return "Happy";
        } else if (selectedRed == 0 && selectedGreen == 0 && selectedBlue == 255) {
            return "Sad";
        } else if (selectedRed == 0 && selectedGreen == 255 && selectedBlue == 0) {
            return "Calm";
        } else {
            return "Lovely";
        }
    }
}