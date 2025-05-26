package com.example.feelsync;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

public class SplashActivity extends AppCompatActivity {

    private static final String APP_NAME = "FEELSYNC";
    private static final long DELAY_BETWEEN_LETTERS = 200;
    private static final long SPLASH_DISPLAY_LENGTH = 3500; // 3.5 seconds

    private LinearLayout letterContainer;
    private TextView subTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        letterContainer = findViewById(R.id.letterContainer);
        subTitleText = findViewById(R.id.subTitleText);

        animateAppName();

        // Navigate to MainActivity after total delay
        new Handler().postDelayed(this::navigateToMainActivity, SPLASH_DISPLAY_LENGTH);
    }

    private void animateAppName() {
        for (int i = 0; i < APP_NAME.length(); i++) {
            final TextView letterView = new TextView(this);
            letterView.setText(String.valueOf(APP_NAME.charAt(i)));
            letterView.setTextSize(32);
            letterView.setTextColor(getColor(android.R.color.white));
            letterView.setAlpha(0f);
            letterContainer.addView(letterView);

            // Delay each letter
            new Handler().postDelayed(() -> {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(letterView, "alpha", 1f);
                fadeIn.setDuration(300);
                fadeIn.start();
            }, i * DELAY_BETWEEN_LETTERS);
        }

        // Show subtitle after all letters are shown
        new Handler().postDelayed(this::animateSubtitle, APP_NAME.length() * DELAY_BETWEEN_LETTERS + 300);
    }

    private void animateSubtitle() {
        subTitleText.setVisibility(android.view.View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(subTitleText, "alpha", 1f);
        fadeIn.setDuration(500);
        fadeIn.start();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close splash activity
    }
}