package com.example.feelsync;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import android.content.SharedPreferences;

import com.example.proglish2.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notesList;
    private final NoteActionListener noteActionListener;
    private static final int MAX_PREVIEW_LENGTH = 30;

    public interface NoteActionListener {
        void deleteNote(Note note);
        void editNote(Note note);
    }

    public NoteAdapter(List<Note> notesList, NoteActionListener listener) {
        this.notesList = notesList;
        this.noteActionListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void updateNotes(List<Note> newNotes) {
        this.notesList = newNotes;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView;
        private final TextView emotionTextView;
        private final TextView noteTextView;
        private final ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text);
            emotionTextView = itemView.findViewById(R.id.emotion_text);
            noteTextView = itemView.findViewById(R.id.note_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(Note note) {
            dateTextView.setText(note.getDate());
            emotionTextView.setText(note.getEmotion());
            emotionTextView.setTextColor(getCustomEmotionColor(itemView.getContext(), note.getEmotion()));

            // Set note preview
            String preview = note.getNote().length() > MAX_PREVIEW_LENGTH ?
                    note.getNote().substring(0, MAX_PREVIEW_LENGTH) + "..." : note.getNote();
            noteTextView.setText(preview);

            // Set click listeners
            itemView.setOnClickListener(v -> openNoteDetail(note));
            deleteButton.setOnClickListener(v -> noteActionListener.deleteNote(note));
        }

        private void openNoteDetail(Note note) {
            Intent intent = new Intent(itemView.getContext(), ViewNoteActivity.class);
            intent.putExtra("NOTE_TEXT", note.getNote());
            intent.putExtra("NOTE_EMOTION", note.getEmotion());
            intent.putExtra("NOTE_DATE", note.getDate());
            itemView.getContext().startActivity(intent);
        }
    }

    // 🔁 Replace hardcoded color logic with dynamic SharedPreferences lookup
    private int getCustomEmotionColor(Context context, String emotion) {
        if (emotion == null) return Color.BLACK;

        SharedPreferences sharedPref = context.getSharedPreferences("ColorPreferences", Context.MODE_PRIVATE);
        String colorKey = "";

        switch (emotion.toLowerCase(Locale.getDefault())) {
            case "angry": colorKey = "colorAngry"; break;
            case "sad": colorKey = "colorSad"; break;
            case "calm": colorKey = "colorCalm"; break;
            case "lovely": colorKey = "colorLove"; break;
            case "happy": colorKey = "colorHappy"; break;
            default: return Color.BLACK;
        }

        // Default fallback colors
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
}