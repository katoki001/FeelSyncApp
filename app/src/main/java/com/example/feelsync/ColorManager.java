package com.example.feelsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ColorManager {
    private static final String PREFS_NAME = "ColorPreferences";

    public static int getColorForEmotion(Context context, String emotion) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        switch (emotion) {
            case "Happy":
                return getSavedColor(sharedPreferences, "colorHappy", Color.YELLOW);
            case "Angry":
                return getSavedColor(sharedPreferences, "colorAngry", Color.RED);
            case "Sad":
                return getSavedColor(sharedPreferences, "colorSad", Color.BLUE);
            case "Calm":
                return getSavedColor(sharedPreferences, "colorCalm", Color.GREEN);
            case "Love":
                return getSavedColor(sharedPreferences, "colorLove", Color.MAGENTA);
            default:
                return Color.WHITE;
        }
    }

    private static int getSavedColor(SharedPreferences sharedPreferences, String key, int defaultColor) {
        return sharedPreferences.getInt(key, defaultColor);
    }

    public static void saveColor(Context context, String key, int color) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, color);
        editor.apply();
    }

    }
