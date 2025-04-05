package com.example.feelsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proglish2.R;

public class ViewNoteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note); // This must be called first

        // Initialize views after setContentView
        TextView noteText = findViewById(R.id.full_note_text);
        TextView emotionText = findViewById(R.id.full_emotion_text);
        TextView dateText = findViewById(R.id.full_date_text);

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String note = intent.getStringExtra("NOTE_TEXT");
            String emotion = intent.getStringExtra("NOTE_EMOTION");
            String date = intent.getStringExtra("NOTE_DATE");

            // Set text to views
            if (note != null) noteText.setText(note);
            if (emotion != null) {
                emotionText.setText(emotion);
                emotionText.setTextColor(getColorFromEmotion(emotion));
            }
            if (date != null) dateText.setText(date);
        }
    }

    private int getColorFromEmotion(String emotion) {
        if (emotion == null) return getResources().getColor(R.color.black);

        switch (emotion) {
            case "Happy": return getResources().getColor(R.color.yellow);
            case "Angry": return getResources().getColor(R.color.red);
            case "Sad": return getResources().getColor(R.color.blue);
            case "Calm": return getResources().getColor(R.color.green);
            case "Lovely": return getResources().getColor(R.color.pink);
            default: return getResources().getColor(R.color.black);
        }
    }
}