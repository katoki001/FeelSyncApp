package com.example.feelsync;

public class Note {
<<<<<<< HEAD
    public String date;
    public String note;
    public String emotion;
    public long timestamp;  // Add timestamp field

    public Note() {}

    // Constructor for easy initialization
    public Note(String date, String emotion, String note, long timestamp) {
        this.date = date;
        this.emotion = emotion;
        this.note = note;
        this.timestamp = timestamp;
=======
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
>>>>>>> 759d1d9 (changes in code)
    }

    // Getters and Setters
    public String getDate() { return date; }
    public String getEmotion() { return emotion; }
<<<<<<< HEAD
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }  // Get timestamp
    public void setDate(String date) { this.date = date; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public void setNote(String note) { this.note = note; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }  // Set timestamp
=======
    public String getNoteText() { return noteText; }
>>>>>>> 759d1d9 (changes in code)
}

