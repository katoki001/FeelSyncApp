package com.example.feelsync;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

public class ViewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);

        TextView fullNoteTextView = findViewById(R.id.full_note_text);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView emotionTextView = findViewById(R.id.emotion_text); // Add this TextView in your layout

        // Get the full note and emotion from the Intent
        String fullNoteText = getIntent().getStringExtra("NOTE_TEXT");
        String emotion = getIntent().getStringExtra("NOTE_EMOTION");

        // Set the full note text
        if (fullNoteText != null) {
            fullNoteTextView.setText(fullNoteText);
        }

        // Set the emotion text
        if (emotion != null) {
            emotionTextView.setText(emotion);
        }
    }
}
