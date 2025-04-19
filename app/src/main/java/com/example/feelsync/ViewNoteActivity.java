package com.example.feelsync;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

public class ViewNoteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);

        TextView noteText = findViewById(R.id.full_note_text);
        TextView emotionText = findViewById(R.id.full_emotion_text);
        TextView dateText = findViewById(R.id.full_date_text);

        String note = getIntent().getStringExtra("NOTE_TEXT");
        String emotion = getIntent().getStringExtra("NOTE_EMOTION");
        String date = getIntent().getStringExtra("NOTE_DATE");

        noteText.setText(note);
        emotionText.setText(emotion);
        emotionText.setTextColor(getColorFromEmotion(emotion));
        dateText.setText(date);
    }

    private int getColorFromEmotion(String emotion) {
        switch (emotion) {
            case "Red/Angry": return getResources().getColor(R.color.red);
            case "Blue/Sad": return getResources().getColor(R.color.blue);
            case "Green/Calm": return getResources().getColor(R.color.green);
            case "Pink/Lovely": return getResources().getColor(R.color.pink);
            case "Yellow/Happy": return getResources().getColor(R.color.yellow);
            default: return getResources().getColor(R.color.black);
        }
    }
}