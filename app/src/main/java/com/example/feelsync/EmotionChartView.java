package com.example.feelsync;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class EmotionChartView extends View {
    private Map<String, Integer> emotionData = new HashMap<>();
    private Paint barPaint, textPaint, axisPaint, bgPaint, valuePaint;
    private RectF barRect;
    private int maxValue = 5;

    private final String[] emotions = {"Happy", "Angry", "Sad", "Calm", "Lovely"};
    private int[] colors = new int[5]; // Dynamic colors

    public EmotionChartView(Context context) {
        super(context);
        init();
        loadColors(context);
    }

    public EmotionChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        loadColors(context);
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#666666"));
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.BLACK);
        valuePaint.setTextSize(32f);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.parseColor("#E0E0E0"));
        axisPaint.setStrokeWidth(3f);

        barRect = new RectF();
    }

    private int getCustomColor(Context context, String emotion) {
        SharedPreferences sharedPref = context.getSharedPreferences("ColorPreferences", Context.MODE_PRIVATE);
        String colorKey = "";

        switch (emotion) {
            case "Happy": colorKey = "colorHappy"; break;
            case "Angry": colorKey = "colorAngry"; break;
            case "Sad": colorKey = "colorSad"; break;
            case "Calm": colorKey = "colorCalm"; break;
            case "Lovely": colorKey = "colorLove"; break;
        }

        if (colorKey.isEmpty()) return Color.WHITE;

        int defaultColor;
        switch (colorKey) {
            case "colorHappy": defaultColor = Color.YELLOW; break;
            case "colorAngry": defaultColor = Color.RED; break;
            case "colorSad": defaultColor = Color.BLUE; break;
            case "colorCalm": defaultColor = Color.GREEN; break;
            case "colorLove": defaultColor = Color.MAGENTA; break;
            default: defaultColor = Color.WHITE;
        }

        int red = sharedPref.getInt(colorKey + "_red", Color.red(defaultColor));
        int green = sharedPref.getInt(colorKey + "_green", Color.green(defaultColor));
        int blue = sharedPref.getInt(colorKey + "_blue", Color.blue(defaultColor));

        return Color.rgb(red, green, blue);
    }

    private void loadColors(Context context) {
        colors[0] = getCustomColor(context, "Happy");
        colors[1] = getCustomColor(context, "Angry");
        colors[2] = getCustomColor(context, "Sad");
        colors[3] = getCustomColor(context, "Calm");
        colors[4] = getCustomColor(context, "Lovely");
    }

    public void refreshColors(Context context) {
        loadColors(context);
        invalidate();
    }

    public void setEmotionData(Map<String, Integer> data) {
        this.emotionData = data != null ? new HashMap<>(data) : new HashMap<>();

        this.maxValue = 5;
        for (int value : this.emotionData.values()) {
            if (value > maxValue) maxValue = value;
        }

        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) return;

        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 16f, 16f, bgPaint);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        canvas.drawLine(padding, padding, padding, height - padding, axisPaint);
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);

        textPaint.setTextSize(24f);
        for (int i = 0; i <= maxValue; i++) {
            float y = height - padding - ((float) (i * chartHeight) / maxValue);
            canvas.drawText(String.valueOf(i), padding - 30, y + 10, textPaint);
            canvas.drawLine(padding - 5, y, width - padding, y, axisPaint);
        }

        int barCount = emotions.length;
        float barWidth = (float) chartWidth / (barCount * 1.5f);
        float spaceWidth = barWidth / 3;

        for (int i = 0; i < barCount; i++) {
            String emotion = emotions[i];
            int value = emotionData.getOrDefault(emotion, 0);
            barPaint.setColor(colors[i]);

            float left = padding + (i * (barWidth + spaceWidth));
            float top = height - padding - (value * chartHeight / maxValue);
            float right = left + barWidth;
            float bottom = height - padding - 1;

            barRect.set(left, top, right, bottom);
            canvas.drawRoundRect(barRect, 8f, 8f, barPaint);

            canvas.drawText(emotion, left + barWidth / 2, height - padding + 30, textPaint);
            if (value > 0) {
                canvas.drawText(String.valueOf(value), left + barWidth / 2, top - 10, valuePaint);
            }
        }
    }

    public String[] getEmotions() {
        return emotions;
    }
}