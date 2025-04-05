package com.example.feelsync;

<<<<<<< HEAD


=======
<<<<<<< HEAD
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
import android.content.Intent;
=======
>>>>>>> 759d1d9 (changes in code)
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notesList;
    private CalendarActivity calendarActivity;

    public NoteAdapter(List<Note> notesList, CalendarActivity calendarActivity) {
        this.notesList = notesList;
        this.calendarActivity = calendarActivity; // Fixed variable name
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

        // Show only a preview of the note (first 30 characters)
        String notePreview = note.note.length() > 30 ?
                note.note.substring(0, 30) + "..." : note.note;
        holder.noteTextView.setText(notePreview);

        // Set text color based on emotion
        holder.emotionTextView.setTextColor(getColorFromEmotion(note.emotion));
<<<<<<< HEAD

        // Set click listener to open full note
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewNoteActivity.class);
            intent.putExtra("NOTE_TEXT", note.note);
            intent.putExtra("NOTE_EMOTION", note.emotion);
            intent.putExtra("NOTE_DATE", note.date);
            v.getContext().startActivity(intent);
        });
<<<<<<< HEAD

        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            calendarActivity.deleteNote(note);
        });
=======
=======
>>>>>>> 759d1d9 (changes in code)
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // Add this method to update the adapter's data
    public void updateNotes(List<Note> newNotes) {
        this.notesList.clear();
        this.notesList.addAll(newNotes);
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, emotionTextView, noteTextView;
        ImageButton deleteButton, editButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text);
            emotionTextView = itemView.findViewById(R.id.emotion_text);
            noteTextView = itemView.findViewById(R.id.note_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    private int getColorFromEmotion(String emotion) {
        if (emotion == null) return Color.BLACK;

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