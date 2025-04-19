package com.example.feelsync;

public class Note {
    public String date;
    public String note;
    public String emotion;
    public long timestamp;  // This should be unique for each note

    // Empty constructor for Firebase
    public Note() {
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor for easy initialization with timestamp
    public Note(String date, String emotion, String note, long timestamp) {
        this.date = date;
        this.emotion = emotion;
        this.note = note;
        this.timestamp = timestamp;
    }

    // Constructor without timestamp (uses current time)
    public Note(String date, String emotion, String note) {
        this.date = date;
        this.emotion = emotion;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }

    // Add this method to safely handle timestamp from Firestore
    public void setFirestoreTimestamp(Long timestamp) {
        this.timestamp = timestamp != null ? timestamp : System.currentTimeMillis();
    }

    // Add proper equals() and hashCode() methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return timestamp == note.timestamp;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(timestamp);
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}