package com.example.feelsync;

public class Note {
    // Constants
    private static final long DEFAULT_TIMESTAMP = System.currentTimeMillis();

    // Fields
    private String date;
    private String note;
    private String emotion;
    private long timestamp; // Unique identifier for each note
    private String documentId; // Firestore document ID for faster updates/deletions

    // Empty constructor for Firebase
    public Note() {
        this.timestamp = DEFAULT_TIMESTAMP;
    }

    // Constructor for easy initialization with timestamp
    public Note(String date, String emotion, String note, long timestamp) {
        this.date = validateField(date, "Date");
        this.emotion = validateField(emotion, "Emotion");
        this.note = validateField(note, "Note");
        this.timestamp = timestamp;
    }

    // Constructor without timestamp (uses current time)
    public Note(String date, String emotion, String note) {
        this.date = validateField(date, "Date");
        this.emotion = validateField(emotion, "Emotion");
        this.note = validateField(note, "Note");
        this.timestamp = DEFAULT_TIMESTAMP;
    }

    // Safely handle timestamp from Firestore
    public void setFirestoreTimestamp(Long timestamp) {
        this.timestamp = timestamp != null ? timestamp : DEFAULT_TIMESTAMP;
    }

    // Set Firestore document ID
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Get Firestore document ID
    public String getDocumentId() {
        return documentId;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = validateField(date, "Date");
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = validateField(note, "Note");
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = validateField(emotion, "Emotion");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Equals and hashCode based on timestamp
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

    // toString() for debugging
    @Override
    public String toString() {
        return "Note{" +
                "date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", emotion='" + emotion + '\'' +
                ", timestamp=" + timestamp +
                ", documentId='" + documentId + '\'' +
                '}';
    }

    // Helper method for field validation
    private String validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }
}