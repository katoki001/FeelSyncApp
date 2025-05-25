package com.example.feelsync;

import android.content.SharedPreferences;
import android.graphics.Color;

public class ColorUtils {

    public static int getDefaultColor(String colorKey) {
        switch (colorKey) {
            case "colorHappy": return Color.YELLOW;
            case "colorAngry": return Color.RED;
            case "colorSad": return Color.BLUE;
            case "colorCalm": return Color.GREEN;
            case "colorLove": return Color.MAGENTA;
            default: return Color.WHITE;
        }
    }

    public static int getSavedColorForKey(SharedPreferences sharedPref, String colorKey) {
        int defaultColor = getDefaultColor(colorKey);
        int red = sharedPref.getInt(colorKey + "_red", Color.red(defaultColor));
        int green = sharedPref.getInt(colorKey + "_green", Color.green(defaultColor));
        int blue = sharedPref.getInt(colorKey + "_blue", Color.blue(defaultColor));
        return Color.rgb(red, green, blue);
    }
}