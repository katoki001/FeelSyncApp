package com.example.feelsync;

public class Note {
<<<<<<< HEAD
    public String date;
    public String note;
    public String emotion;
    public long timestamp;  // This should be unique for each note

<<<<<<< HEAD
    public Note() {
        this.timestamp = System.currentTimeMillis(); // Set default timestamp
=======
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
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
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
<<<<<<< HEAD
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }
    public void setDate(String date) { this.date = date; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public void setNote(String note) { this.note = note; }
<<<<<<< HEAD
=======
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }  // Set timestamp
=======
    public String getNoteText() { return noteText; }
>>>>>>> 759d1d9 (changes in code)
}
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

}