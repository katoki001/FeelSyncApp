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

    // Declare buttons for Sign Up, Log In, and Log Out
    private Button signUpButton, logInButton, logOutButton;

    // Firebase Authentication instance
    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_signin_signup_main);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Link buttons from the layout XML file to Java variables
        signUpButton = findViewById(R.id.signbtn);
        logInButton = findViewById(R.id.logbtn);
        logOutButton = findViewById(R.id.logOutbtn);

        // Set OnClickListener for the Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Sign Up button is clicked, start the SignUpActivity
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent); // Start the SignUpActivity
            }
        });

        // Set OnClickListener for the Log In button
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the Log In button is clicked, start the LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent); // Start the LoginActivity
            }
        });

        // Set OnClickListener for the Log Out button
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user
                auth.signOut();

                // Redirect to the LoginActivity and clear the back stack
                Intent intent = new Intent(MainActivity.this, LogOutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}


