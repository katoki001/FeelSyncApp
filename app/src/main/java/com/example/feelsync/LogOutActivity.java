package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogOutActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logOutbtn);
        textView = findViewById(R.id.email);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(LogOutActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText("Logged in as: " + user.getEmail());
        }


        // Set up logout button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LogOutActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }}
