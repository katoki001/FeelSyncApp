package com.example.feelsync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat_history.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_CHATS = "chats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SESSION_ID = "session_id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_IS_USER = "is_user";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHATS_TABLE = "CREATE TABLE " + TABLE_CHATS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SESSION_ID + " TEXT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_IS_USER + " INTEGER,"
                + COLUMN_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_CHATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATS);
        onCreate(db);
    }

    public void addMessage(String sessionId, ChatMessage message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_ID, sessionId);
        values.put(COLUMN_MESSAGE, message.getMessage());
        values.put(COLUMN_IS_USER, message.isUser() ? 1 : 0);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_CHATS, null, values);
        db.close();
    }

    public List<ChatSession> getAllSessions() {
        List<ChatSession> sessions = new ArrayList<>();
        String query = "SELECT DISTINCT " + COLUMN_SESSION_ID + " FROM " + TABLE_CHATS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String sessionId = cursor.getString(0);
                sessions.add(new ChatSession(sessionId, getSessionMessages(sessionId)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessions;
    }

    public List<ChatMessage> getSessionMessages(String sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CHATS + " WHERE " + COLUMN_SESSION_ID + " = ? ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{sessionId});

        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                boolean isUser = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_USER)) == 1;
                messages.add(new ChatMessage(message, isUser));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    public void deleteSession(String sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHATS, COLUMN_SESSION_ID + " = ?", new String[]{sessionId});
        db.close();
    }
}