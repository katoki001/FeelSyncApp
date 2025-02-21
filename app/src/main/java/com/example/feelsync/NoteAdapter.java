package com.example.feelsync;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notesList;

    public NoteAdapter(List<Note> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.dateTextView.setText(note.date);
        holder.emotionTextView.setText(note.emotion);
        holder.noteTextView.setText(note.note);

        // Set text color based on emotion
        holder.emotionTextView.setTextColor(getColorFromEmotion(note.emotion));

        // Set click listener to open full note on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewNoteActivity.class);
            intent.putExtra("NOTE_TEXT", note.note); // Pass full note text
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, emotionTextView, noteTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text);
            emotionTextView = itemView.findViewById(R.id.emotion_text);
            noteTextView = itemView.findViewById(R.id.note_text);
        }
    }

    private int getColorFromEmotion(String emotion) {
        switch (emotion) {
            case "Red/Angry": return Color.RED;
            case "Blue/Sad": return Color.BLUE;
            case "Green/Calm": return Color.GREEN;
            case "Pink/Lovely": return Color.MAGENTA;
            case "Yellow/Happy": return Color.YELLOW;
            default: return Color.BLACK;
        }
    }
}
