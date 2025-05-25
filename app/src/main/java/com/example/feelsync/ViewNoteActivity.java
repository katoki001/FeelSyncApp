package com.example.feelsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

import java.util.Locale;

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
        emotionText.setTextColor(getCustomEmotionColor(emotion));
        dateText.setText(date);
    }

    private int getCustomEmotionColor(String emotion) {
        if (emotion == null) return Color.BLACK;

        SharedPreferences sharedPref = getSharedPreferences("ColorPreferences", Context.MODE_PRIVATE);
        String colorKey = "";

        String normalized = emotion.toLowerCase(Locale.getDefault());

        switch (normalized) {
            case "angry": colorKey = "colorAngry"; break;
            case "sad": colorKey = "colorSad"; break;
            case "calm": colorKey = "colorCalm"; break;
            case "love":
            case "lovely": colorKey = "colorLove"; break;
            case "happy": colorKey = "colorHappy"; break;
            default: return Color.BLACK;
        }

        return ColorUtils.getSavedColorForKey(sharedPref, colorKey);
    }
}