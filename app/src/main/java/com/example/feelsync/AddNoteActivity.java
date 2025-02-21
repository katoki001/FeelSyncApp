package com.example.feelsync;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private EditText noteInput;
    private Button uploadButton, saveButton;
    private String selectedColor = "";
    private static final int IMAGE_REQUEST_CODE = 1;
    private String selectedDate;
    private FirebaseFirestore db;
    private Uri imageUri;
    private String base64Image = null; // Store Base64 string of the image

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
        uploadButton = findViewById(R.id.upload_button);
        saveButton = findViewById(R.id.save_button);

        colorHappy.setOnClickListener(v -> selectColor("Happy"));
        colorAngry.setOnClickListener(v -> selectColor("Angry"));
        colorSad.setOnClickListener(v -> selectColor("Sad"));
        colorCalm.setOnClickListener(v -> selectColor("Calm"));
        colorLove.setOnClickListener(v -> selectColor("Lovely"));

        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        uploadButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveData());
    }

    private void selectColor(String color) {
        selectedColor = color;
        Toast.makeText(this, "Color selected: " + selectedColor, Toast.LENGTH_SHORT).show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            convertImageToBase64();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void convertImageToBase64() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to convert image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        String note = noteInput.getText().toString().trim();

        if (selectedColor.isEmpty()) {
            Toast.makeText(this, "Please select an emotion color.", Toast.LENGTH_SHORT).show();
        } else if (note.isEmpty()) {
            Toast.makeText(this, "Please write a note.", Toast.LENGTH_SHORT).show();
        } else {
            saveNoteToFirestore(note, base64Image);
        }
    }

    private void saveNoteToFirestore(String note, String base64Image) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("date", selectedDate);
        noteData.put("emotion", selectedColor);
        noteData.put("note", note);
        if (base64Image != null) {
            noteData.put("image", base64Image);
        }

        // Save note to Firestore
        db.collection("notes").add(noteData)
                .addOnSuccessListener(documentReference -> {
                    String noteId = documentReference.getId(); // Get the Firestore document ID
                    Intent intent = new Intent(AddNoteActivity.this, ViewNoteActivity.class);
                    intent.putExtra("NOTE_ID", noteId);  // Pass the noteId to ViewNoteActivity
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show());

    }
}
