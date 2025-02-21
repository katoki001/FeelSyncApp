package com.example.feelsync;

public class Note {
    public String date;
    public String note;
    public String emotion;

    public Note() {}

    // Constructor for easy initialization
    public Note(String date, String emotion, String note) {
        this.date = date;
        this.emotion = emotion;
        this.note = note;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public String getEmotion() { return emotion; }
    public String getNote() { return note; }
    public void setDate(String date) { this.date = date; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public void setNote(String note) { this.note = note; }
}

