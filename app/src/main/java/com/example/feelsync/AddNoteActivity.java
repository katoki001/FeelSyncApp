package com.example.feelsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {
    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private EditText noteInput;
    private Button uploadButton, saveButton;
    private String selectedColor = "";
    private static final int IMAGE_REQUEST_CODE = 1;  // For image picking
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Initialize Views
        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
        noteInput = findViewById(R.id.note_input);
        uploadButton = findViewById(R.id.upload_button);
        saveButton = findViewById(R.id.save_button);

        // Set color selection listeners
        colorHappy.setOnClickListener(v -> selectColor("Yellow/Happy"));
        colorAngry.setOnClickListener(v -> selectColor("Red/Angry"));
        colorSad.setOnClickListener(v -> selectColor("Blue/Sad"));
        colorCalm.setOnClickListener(v -> selectColor("Green/Calm"));
        colorLove.setOnClickListener(v -> selectColor("Pink/Lovely"));

        // Get selected date from the previous activity
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        // Set upload button listener (image picker)
        uploadButton.setOnClickListener(v -> openImagePicker());

        // Set save button listener
        saveButton.setOnClickListener(v -> saveData());
    }

    // Handle color selection
    private void selectColor(String color) {
        selectedColor = color;
        Toast.makeText(this, "Color selected: " + selectedColor, Toast.LENGTH_SHORT).show();
    }

    // Open image picker for image upload
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Image was selected
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Save the selected data (color, note, and image)
    private void saveData() {
        String note = noteInput.getText().toString().trim();

        if (selectedColor.isEmpty()) {
            Toast.makeText(this, "Please select an emotion color.", Toast.LENGTH_SHORT).show();
        } else if (note.isEmpty()) {
            Toast.makeText(this, "Please write a note.", Toast.LENGTH_SHORT).show();
        } else {
            // Send data back to the calendar activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EMOTION_COLOR", selectedColor);
            resultIntent.putExtra("NOTE", note);
            resultIntent.putExtra("SELECTED_DATE", selectedDate);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}