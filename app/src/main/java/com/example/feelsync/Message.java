
package com.example.feelsync;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Message extends AppCompatActivity {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT = "bot";

    String message;
    String sentBy;

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}
