package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //--------------------------------------------------------------------------------------------//
    //SIGN UP, SIGN IN, LOG OUT, OPEN//
    //--------------------------------------------------------------------------------------------//
    private Button signUpButton, logInButton, logOutButton;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup_main);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize buttons for authentication actions
        signUpButton = findViewById(R.id.signbtn);
        logInButton = findViewById(R.id.logbtn);
        logOutButton = findViewById(R.id.logOutbtn);

        // Set click listeners for authentication buttons
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent); // Start the SignUpActivity
        });

        logInButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent); // Start the LoginActivity
        });

        logOutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LogOutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); // Start the LogOutActivity
        });

    }
}