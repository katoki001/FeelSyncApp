package com.example.feelsync;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        // Initialize Bottom Navigation
        setupBottomNavigation();

        // Populate Emotional Advice Cards
        populateAdviceCards();

        // Animate the advice cards
        animateCards();

    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            }

            Class<?> targetActivity = null;
            if (id == R.id.nav_calendar) {
                targetActivity = CalendarActivity.class;
            } else if (id == R.id.nav_ai) {
                targetActivity = AIChatActivity.class;
            } else if (id == R.id.nav_settings) {
                targetActivity = SettingsActivity.class;
            } else if (id == R.id.nav_music) {
                targetActivity = MusicActivity.class;
            }

            if (targetActivity != null) {
                navigateToActivity(targetActivity);
                return true;
            }

            return false;
        });
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void populateAdviceCards() {
        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        // List of advice messages
        List<String> adviceMessages = Arrays.asList(
                "It's okay to feel sad sometimes. Let yourself feel it—it's part of healing.",
                "Even on the darkest days, there’s always a glimmer of light waiting for you.",
                "You’re stronger than you think. This moment will pass.",
                "If you're feeling overwhelmed, take a deep breath. You’ve got this.",
                "Sometimes crying is the best way to release what’s been weighing you down.",
                "Sadness doesn’t define you—it’s just a chapter in your story.",
                "Take a break and do something that brings you joy, even if it’s small.",
                "You don’t have to face this alone—reach out to someone who cares about you.",
                "The sun will rise again, and so will you.",
                "Feeling low is temporary; your strength is permanent.",
                "When you're anxious, focus on counting things around you—it helps ground you.",
                "Breathe in slowly for four counts, hold for four, and exhale for four. Repeat.",
                "Anxiety is just a visitor—it doesn’t have to stay forever.",
                "Focus on one thing at a time. Progress is progress, no matter how small.",
                "You’ve survived every hard day so far—you’ll survive today too.",
                "Write down your worries. Sometimes seeing them on paper makes them less scary.",
                "It’s okay to step away from overwhelming situations. Take care of yourself first.",
                "Remind yourself: I am safe. I am calm. I am in control.",
                "One step at a time. You don’t need to figure everything out right now.",
                "Remember, tomorrow is a new day with endless possibilities.",
                "Break big tasks into smaller ones—one step at a time.",
                "Stress is normal, but don’t let it define your day.",
                "Take a walk outside. Fresh air can work wonders.",
                "Prioritize rest—it’s not laziness; it’s self-care.",
                "Celebrate small victories—they add up to big achievements.",
                "Delegate when possible. You don’t have to carry everything alone.",
                "Drink water and stretch. Your body and mind will thank you.",
                "Pause and ask yourself: What’s the next best thing I can do?",
                "It’s okay to say no. Protecting your energy is important.",
                "Progress over perfection. Just keep moving forward.",
                "You are capable of amazing things—even if you don’t see it yet.",
                "Every effort counts. Keep going!",
                "Believe in yourself as much as others believe in you.",
                "Success isn’t always immediate, but persistence pays off.",
                "You’re closer to your goals than you were yesterday.",
                "Don’t compare your beginning to someone else’s middle.",
                "Keep pushing. The view at the top is worth it.",
                "Small steps lead to big changes. Trust the process.",
                "Mistakes are proof that you’re trying. That’s something to be proud of.",
                "Focus on growth, not perfection.",
                "Be grateful for the little things—they make life beautiful.",
                "Today, appreciate the people who bring light into your life.",
                "Gratitude turns what we have into enough.",
                "Find three things to be thankful for each day.",
                "Even in tough times, there’s always something to be grateful for.",
                "Appreciate where you are right now—it’s part of your journey.",
                "Thank yourself for all the hard work you’ve done.",
                "A grateful heart is a magnet for miracles.",
                "Celebrate the present moment—it’s a gift.",
                "Gratitude gives you the power to transform challenges into opportunities.",
                "Rest is productive. Give yourself permission to recharge.",
                "Do something today that makes your soul happy.",
                "Self-care isn’t selfish—it’s essential.",
                "Treat yourself with kindness. You deserve it.",
                "Take a bath, read a book, or listen to music—whatever soothes your soul.",
                "Boundaries are an act of self-love. Set them without guilt.",
                "Your mental health matters more than anything else.",
                "Nourish your body with healthy food—it fuels your spirit.",
                "Speak kindly to yourself. Words have power.",
                "Pause and reflect: How can I show myself love today?",
                "You are enough, exactly as you are.",
                "Trust your instincts—they know more than you think.",
                "Confidence comes from embracing who you truly are.",
                "You’re unique, and that’s your superpower.",
                "The world needs your voice. Share it boldly.",
                "Doubt means you’re growing. Keep going.",
                "Comparison steals joy. Focus on your own path.",
                "Stand tall. You are unstoppable.",
                "Believe in your potential—you’re capable of greatness.",
                "Fake it till you make it. Confidence grows with practice.",
                "Every storm runs out of rain. So will this one.",
                "Resilience is built through challenges. You’re becoming stronger every day.",
                "You’ve overcome tough times before—you’ll overcome this too.",
                "Life is unpredictable, but so are you.",
                "Adaptability is your greatest strength.",
                "Fall seven times, stand up eight.",
                "Challenges shape us into better versions of ourselves.",
                "The only failure is giving up.",
                "What doesn’t kill you makes you stronger.",
                "Perseverance is the key to success.",
                "Better days are ahead. Hold on tight.",
                "Hope is the anchor that keeps us steady in rough waters.",
                "There’s always a reason to smile, even on the hardest days.",
                "Every ending is a new beginning.",
                "The future holds endless possibilities.",
                "Stay hopeful—it’s the fuel that keeps dreams alive.",
                "Rainbows appear after storms. Keep looking up.",
                "Hope whispers: Things will get better.",
                "Even in darkness, stars still shine.",
                "Tomorrow is a blank canvas. Paint it however you want.",
                "You’re doing great, even if you don’t feel like it.",
                "One small step forward is still progress.",
                "You’re braver than you believe and stronger than you seem.",
                "Don’t give up—it’s often darkest before the dawn.",
                "You inspire others simply by being you.",
                "Keep going. Greatness lies ahead.",
                "Every day is a chance to start fresh.",
                "You’re not alone. We’re all rooting for you.",
                "You’re capable of achieving incredible things.",
                "The best is yet to come. Keep believing."
        );

        // Shuffle the list and take the first 5 messages
        Collections.shuffle(adviceMessages);

        // Add the first set of cards (5 unique cards)
        for (int i = 0; i < 7; i++) {
            String message = adviceMessages.get(i);
            View cardView = LayoutInflater.from(this).inflate(R.layout.card_item, cardContainer, false);
            TextView textView = cardView.findViewById(R.id.cardText);
            textView.setText(message);
            cardContainer.addView(cardView);
        }

        // Add a duplicate set of the same 5 cards for seamless looping
        for (int i = 0; i < 7; i++) {
            String message = adviceMessages.get(i);
            View cardView = LayoutInflater.from(this).inflate(R.layout.card_item, cardContainer, false);
            TextView textView = cardView.findViewById(R.id.cardText);
            textView.setText(message);
            cardContainer.addView(cardView);
        }
    }

    private void animateCards() {
        LinearLayout cardContainer = findViewById(R.id.cardContainer);

        cardContainer.post(() -> {
            int totalCardWidth = getTotalCardsWidth(cardContainer); // Width of all 21 cards
            int singleSetWidth = totalCardWidth / 3; // Since we added 3 copies

            ValueAnimator animator = ValueAnimator.ofFloat(0, -singleSetWidth);
            animator.setDuration(60000); // Adjust speed as needed
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());

            animator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                cardContainer.setTranslationX(value);

                // When half the width has been scrolled, reset position
                if (value <= -singleSetWidth) {
                    cardContainer.setTranslationX(0); // Reset instantly
                    animator.setCurrentPlayTime(0); // Restart animation from beginning
                }
            });

            animator.start();
        });
    }

    // Helper method to calculate total width of all child views
    private int getTotalCardsWidth(LinearLayout container) {
        int width = 0;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            int childWidth = child.getWidth();
            if (childWidth == 0) {
                child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                childWidth = child.getMeasuredWidth();
            }
            width += childWidth;
        }
        return width;
    }

    }
