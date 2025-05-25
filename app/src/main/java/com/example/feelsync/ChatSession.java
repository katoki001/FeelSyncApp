// ChatSession.java
package com.example.feelsync;

import java.util.List;

public class ChatSession {
    private String sessionId;
    private List<ChatMessage> messages;

    public ChatSession(String sessionId, List<ChatMessage> messages) {
        this.sessionId = sessionId;
        this.messages = messages;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String getPreviewText() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getMessage();
        }
        return "";
    }

    public String getDate() {
        // You can format the date based on timestamp if you store it
        return "Today"; // Placeholder
    }
}