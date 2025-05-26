package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //--------------------------------------------------------------------------------------------//
    // SIGN UP, SIGN IN, LOG OUT, OPEN //
    //--------------------------------------------------------------------------------------------//
    private Button signUpButton, logInButton;
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, go to main app (e.g., HomeActivity)
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent returning back
            return;
        }

        // Set the content view only if user is NOT logged in
        setContentView(R.layout.activity_signin_signup_main);

        // Initialize buttons for authentication actions
        signUpButton = findViewById(R.id.signbtn);
        logInButton = findViewById(R.id.logbtn);

        // Set click listeners for authentication buttons
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent); // Start the SignUpActivity
        });

        logInButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent); // Start the LoginActivity
        });
    }
}