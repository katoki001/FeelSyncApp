package com.example.feelsync;

public class Note {
    public int note;
    String date;
    String emotion;
    String noteText;

    // Empty constructor for Firebase
    public Note() {}

    // Constructor for easy initialization
    public Note(String date, String emotion, String noteText) {
        this.date = date;
        this.emotion = emotion;
        this.noteText = noteText;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public String getEmotion() { return emotion; }
    public String getNoteText() { return noteText; }
}

