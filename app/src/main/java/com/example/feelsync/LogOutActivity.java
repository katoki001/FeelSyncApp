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

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Link UI elements
        button = findViewById(R.id.logOutbtn);
        textView = findViewById(R.id.username);

        // Get the current user
        user = auth.getCurrentUser();

        // If no user is logged in, redirect to SignUpActivity
        if (user == null) {
            redirectToSignUp("No user is logged in. Please sign up.");
            return; // Prevent further execution in this activity
        } else {
            // Display the user's email
            textView.setText("Logged in as: " + user.getEmail());
        }

        // Set up logout button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirm with the user before deleting the account
                // (optional: add a confirmation dialog)
                deleteAccount();
            }
        });
    }

    private void deleteAccount() {
        FirebaseUser currentUser = auth.getCurrentUser();

        // Ensure the user is logged in
        if (currentUser != null) {
            // Reauthenticate using email and password
            String email = currentUser.getEmail();
            String password = "user_password"; // Retrieve the user's password (e.g., from a password input field)

            // Create credentials using email and password
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            // Reauthenticate the user
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete the user account
                            currentUser.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            // Sign out the user
                                            auth.signOut();
                                            Toast.makeText(LogOutActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();

                                            // Redirect to SignUpActivity after account deletion
                                            redirectToSignUp("Your account has been deleted. Please sign up again.");
                                        } else {
                                            Toast.makeText(LogOutActivity.this, "Error deleting account: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(LogOutActivity.this, "Reauthentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(LogOutActivity.this, "No user is logged in.", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to redirect to SignUpActivity
    private void redirectToSignUp(String message) {
        Intent intent = new Intent(LogOutActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("LOGOUT_MESSAGE", message); // Pass the message to the SignUpActivity
        startActivity(intent);
        finish();
    }
}
