package com.example.feelsync;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public enum Sender {
        USER("me"),
        BOT("bot");

        private final String value;

        Sender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Sender fromString(String text) {
            for (Sender sender : Sender.values()) {
                if (sender.value.equalsIgnoreCase(text)) {
                    return sender;
                }
            }
            return BOT;
        }
    }

    private String id;
    private String content;
    private Sender sender;
    private long timestamp;
    private boolean isRead;

    public Message(String content, Sender sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.id = "msg_" + timestamp + "_" + (sender == Sender.USER ? "u" : "b");
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public Sender getSender() { return sender; }
    public long getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public boolean isSentByUser() { return sender == Sender.USER; }
    public void markAsRead() { isRead = true; }

    public String getFormattedTime() {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(timestamp));
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sender=" + sender +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}