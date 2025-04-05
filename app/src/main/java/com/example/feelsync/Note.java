package com.example.feelsync;

public class Note {
    public String date;
    public String note;
    public String emotion;
    public long timestamp;  // This should be unique for each note

    public Note() {
        this.timestamp = System.currentTimeMillis(); // Set default timestamp
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
    public String getDate() { return date; }
    public String getEmotion() { return emotion; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }
    public void setDate(String date) { this.date = date; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public void setNote(String note) { this.note = note; }

}