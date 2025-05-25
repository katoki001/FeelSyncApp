package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "SettingsActivity";
    private static final String PREFS_NAME = "theme_prefs";
    private static final String DARK_MODE_KEY = "dark_mode_enabled";

    // Views
    private EditText userName, userDescription;
    private TextView userEmail;
    private SwitchCompat darkModeSwitch;
    private ImageView profileImageView;
    private ProgressBar profileProgressBar;
    private Button saveChangesButton;
    private BottomNavigationView bottomNavigationView;

    // Navigation Arrows
    private ImageView notificationArrow, logoutArrow, chooseColorArrow;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    // Image Picker
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        }

        // Initialize views and functionality
        initializeViews();
        setupBottomNavigation();
        setClickListeners();
        setupDarkModeSwitch();

        // Load user profile data
        loadUserProfile();
    }

    /**
     * Initializes all UI components.
     */
    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        try {
            // Find views by ID
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            userName = findViewById(R.id.user_name);
            userEmail = findViewById(R.id.user_email);
            userDescription = findViewById(R.id.user_description);
            profileImageView = findViewById(R.id.profile_image);
            darkModeSwitch = findViewById(R.id.dark_mode_switch);
            profileProgressBar = findViewById(R.id.profile_progress);
            saveChangesButton = findViewById(R.id.save_changes_button);

            // Navigation arrows
            notificationArrow = findViewById(R.id.notification_arrow);
            logoutArrow = findViewById(R.id.logout_arrow);
            chooseColorArrow = findViewById(R.id.choose_color_arrow);

            // Validate critical views
            if (userName == null || userDescription == null || profileImageView == null || saveChangesButton == null) {
                Log.e(TAG, "One or more views are null. Check XML layout IDs.");
                Toast.makeText(this, "Error initializing UI", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load profile image from internal storage
            Bitmap profileBitmap = loadProfileImageFromInternalStorage();
            if (profileBitmap != null) {
                profileImageView.setImageBitmap(profileBitmap);
            } else {
                profileImageView.setImageResource(R.drawable.default_profile); // Fallback to default image
            }

            // Set initial visibility
            userName.setVisibility(View.INVISIBLE);
            userEmail.setVisibility(View.INVISIBLE);
            userDescription.setVisibility(View.INVISIBLE);
            profileProgressBar.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing UI", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up click listeners for interactive components.
     */
    private void setClickListeners() {
        // Profile image picker
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelection
        );
        profileImageView.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Save changes button
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());

        // Notification arrow
        if (notificationArrow != null) {
            notificationArrow.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, NotificationActivity.class);
                startActivity(intent);
            });
        }

        // Logout arrow
        if (logoutArrow != null) {
            logoutArrow.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        // Choose color arrow
        if (chooseColorArrow != null) {
            chooseColorArrow.setOnClickListener(v -> {
                Intent intent = new Intent(SettingsActivity.this, ChooseColorActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Handles saving profile changes (name and description) to Firebase.
     */
    private void saveProfileChanges() {
        String newName = userName.getText().toString().trim();
        String newDescription = userDescription.getText().toString().trim();

        // Validate name field
        if (newName.isEmpty()) {
            userName.setError("Name cannot be empty");
            return;
        }

        // Show progress
        profileProgressBar.setVisibility(View.VISIBLE);
        saveChangesButton.setEnabled(false);

        // Create/update user profile in Firebase
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("description", newDescription);

        userRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    profileProgressBar.setVisibility(View.GONE);
                    saveChangesButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to update profile", task.getException());
                    }
                });
    }

    /**
     * Handles image selection and processing.
     *
     * @param uri The URI of the selected image.
     */
    private void handleImageSelection(Uri uri) {
        if (uri == null) {
            Log.w(TAG, "No image selected");
            return;
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (originalBitmap != null) {
                int width = originalBitmap.getWidth();
                int height = originalBitmap.getHeight();
                int newEdge = Math.min(width, height);
                int xOffset = (width - newEdge) / 2;
                int yOffset = (height - newEdge) / 2;

                // Crop the image to a square
                Bitmap squareBitmap = Bitmap.createBitmap(originalBitmap, xOffset, yOffset, newEdge, newEdge);

                // Scale the image to 80dp
                float density = getResources().getDisplayMetrics().density;
                int sizePx = (int) (80 * density);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(squareBitmap, sizePx, sizePx, true);

                // Update the ImageView and save to internal storage
                profileImageView.setImageBitmap(scaledBitmap);
                saveProfileImageToInternalStorage(scaledBitmap);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to process image", e);
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the profile image to internal storage.
     *
     * @param bitmap The processed image bitmap.
     */
    private void saveProfileImageToInternalStorage(Bitmap bitmap) {
        try {
            FileOutputStream fos = openFileOutput("profile_image.jpg", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Log.d(TAG, "Profile image saved to internal storage");
        } catch (IOException e) {
            Log.e(TAG, "Failed to save profile image to internal storage", e);
        }
    }

    /**
     * Loads the profile image from internal storage.
     *
     * @return The loaded bitmap or null if the file doesn't exist.
     */
    private Bitmap loadProfileImageFromInternalStorage() {
        try {
            File file = new File(getFilesDir(), "profile_image.jpg");
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load profile image from internal storage", e);
        }
        return null;
    }

    /**
     * Sets up the dark mode switch.
     */
    private void setupDarkModeSwitch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(DARK_MODE_KEY, false);
        darkModeSwitch.setChecked(isDarkMode);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(DARK_MODE_KEY, isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    /**
     * Sets up the bottom navigation view.
     */
    private void setupBottomNavigation() {
        if (bottomNavigationView == null) {
            Log.e(TAG, "BottomNavigationView not found");
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Class<?> destinationActivity = null;

            if (id == R.id.nav_calendar) {
                destinationActivity = CalendarActivity.class;
            } else if (id == R.id.nav_home) {
                destinationActivity = MainPageActivity.class;
            } else if (id == R.id.nav_music) {
                destinationActivity = MusicActivity.class;
            } else if (id == R.id.nav_ai) {
                destinationActivity = AIChatActivity.class;
            } else if (id == R.id.nav_settings) {
                return true; // Already on settings
            }

            if (destinationActivity != null) {
                startActivity(new Intent(this, destinationActivity));
                finish();
                return true;
            }
            return false;
        });
    }

    /**
     * Loads user profile data from Firebase.
     */
    private void loadUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set email from Firebase Auth
        userEmail.setText(currentUser.getEmail());

        // Load other profile data from Firebase Database
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    if (name != null) userName.setText(name);
                    if (description != null) userDescription.setText(description);
                } else {
                    // Set default values if no data exists
                    userName.setText("New User");
                    userDescription.setText("Tell us about yourself");
                }

                // Make views visible
                userName.setVisibility(View.VISIBLE);
                userEmail.setVisibility(View.VISIBLE);
                userDescription.setVisibility(View.VISIBLE);
                profileProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user profile", error.toException());
                Toast.makeText(SettingsActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                profileProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFinishing() && !isDestroyed()) {
            loadUserProfile();
        }
    }
}