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
    //SIGN UP, SIGN IN, LOG OUT//
    //--------------------------------------------------------------------------------------------//
    private Button signUpButton, logInButton, logOutButton, openButton;
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
        openButton = findViewById(R.id.Open);

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

        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent); //Just opening the main page
        });



        //--------------------------------------------------------------------------------------------//
        //ACTIVITY MAIN XML JAVA CODES//
        //--------------------------------------------------------------------------------------------//
        setContentView(R.layout.activity_mainpage); // Replace with your XML file name if different

        // Initialize buttons for main activity actions
        Button instructionBtn = findViewById(R.id.Instructionbtn);
        Button settingsBtn = findViewById(R.id.Settingsbtn);
        Button aiBtn = findViewById(R.id.Aibtn);
        Button calendarBtn = findViewById(R.id.Calendarbtn);
        Button musicBtn = findViewById(R.id.Musicbtn);
        Button triggerBtn = findViewById(R.id.Trigerbtn);

        // Set click listeners for each button
        /*instructionBtn.setOnClickListener(v -> {
            // Add your logic for the Instruction button
            Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
            startActivity(intent);
        });

        settingsBtn.setOnClickListener(v -> {
            // Add your logic for the Settings button
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        aiBtn.setOnClickListener(v -> {
            // Add your logic for the AI Chat button
            Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
            startActivity(intent);
        });*/

        calendarBtn.setOnClickListener(v -> {
            // Add your logic for the Calendar button
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        /*musicBtn.setOnClickListener(v -> {
            // Add your logic for the Music button
            Intent intent = new Intent(MainActivity.this, MusicActivity.class);
            startActivity(intent);
        });

        triggerBtn.setOnClickListener(v -> {
            // Add your logic for the Triggers button
            Intent intent = new Intent(MainActivity.this, TriggerActivity.class);
            startActivity(intent);
        });*/
    }
}