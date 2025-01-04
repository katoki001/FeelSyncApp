package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private ProgressBar progressBar;
    private Button signInButton;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Link UI components
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.signinbtn);
        progressBar = findViewById(R.id.progressBar);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the email and password entered by the user
                String email = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    usernameEditText.setError("Email is required");
                    usernameEditText.requestFocus();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    usernameEditText.setError("Enter a valid email address");
                    usernameEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters");
                    passwordEditText.requestFocus();
                    return;
                }

                // Show progress bar and disable button
                progressBar.setVisibility(View.VISIBLE);
                signInButton.setEnabled(false);

                // Sign in with Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                signInButton.setEnabled(true);

                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    // Redirect to MainActivity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
