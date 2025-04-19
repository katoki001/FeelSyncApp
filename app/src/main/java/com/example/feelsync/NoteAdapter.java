package com.example.feelsync;

import android.content.Intent;
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
    private final NoteActionListener noteActionListener;
    private static final int MAX_PREVIEW_LENGTH = 30;

    public interface NoteActionListener {
        void deleteNote(Note note);
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

            // Remove any edit button references from your item_note.xml
        }

        public void bind(Note note) {
            dateTextView.setText(note.date);
            emotionTextView.setText(note.emotion);
            emotionTextView.setTextColor(getEmotionColor(note.emotion));

            // Set note preview
            String preview = note.note.length() > MAX_PREVIEW_LENGTH ?
                    note.note.substring(0, MAX_PREVIEW_LENGTH) + "..." : note.note;
            noteTextView.setText(preview);

            // Set click listeners
            itemView.setOnClickListener(v -> openNoteDetail(note));
            deleteButton.setOnClickListener(v -> noteActionListener.deleteNote(note));
        }

        private void openNoteDetail(Note note) {
            Intent intent = new Intent(itemView.getContext(), ViewNoteActivity.class);
            intent.putExtra("NOTE_TEXT", note.note);
            intent.putExtra("NOTE_EMOTION", note.emotion);
            intent.putExtra("NOTE_DATE", note.date);
            itemView.getContext().startActivity(intent);
        }
    }

    private int getEmotionColor(String emotion) {
        if (emotion == null) return Color.BLACK;

        switch (emotion.toLowerCase()) {
            case "red/angry":
            case "angry":
                return Color.RED;
            case "blue/sad":
            case "sad":
                return Color.BLUE;
            case "green/calm":
            case "calm":
                return Color.GREEN;
            case "pink/lovely":
            case "lovely":
                return Color.MAGENTA;
            case "yellow/happy":
            case "happy":
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
}