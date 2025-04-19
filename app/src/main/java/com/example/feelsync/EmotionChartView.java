package com.example.feelsync;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class EmotionChartView extends View {
    private Map<String, Integer> emotionData = new HashMap<>();
    private Paint barPaint, textPaint, axisPaint, bgPaint, valuePaint;
    private RectF barRect;
    private int maxValue = 5;

    private final String[] emotions = {"Happy", "Angry", "Sad", "Calm", "Lovely"};
    private final int[] defaultColors = {
            Color.parseColor("#FFDF55"), // Happy (yellow)
            Color.parseColor("#FF0000"), // Angry (red)
            Color.parseColor("#0000FF"), // Sad (blue)
            Color.parseColor("#00FF00"), // Calm (green)
            Color.parseColor("#FF69B4")  // Lovely (pink)
    };

    public EmotionChartView(Context context) {
        super(context);
        init();
    }

    public EmotionChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
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

    public void setEmotionData(Map<String, Integer> data) {
        this.emotionData = data != null ? new HashMap<>(data) : new HashMap<>();

        // Calculate max value (minimum of 5 for better visualization)
        this.maxValue = 5;
        for (int value : this.emotionData.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) return;

        // Draw background
        float cornerRadius = 16f;
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, bgPaint);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        // Draw axes
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint);
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);

        // Draw Y-axis labels
        textPaint.setTextSize(24f);
        for (int i = 0; i <= maxValue; i++) {
            float y = height - padding - (i * chartHeight / maxValue);
            canvas.drawText(String.valueOf(i), padding - 30, y + 10, textPaint);
            canvas.drawLine(padding - 5, y, width - padding, y, axisPaint);
        }

        int barCount = emotions.length;
        float barWidth = (float) chartWidth / (barCount * 1.5f);
        float spaceWidth = barWidth / 3;

        // Draw bars
        boolean hasData = false;
        for (int i = 0; i < barCount; i++) {
            String emotion = emotions[i];
            int value = emotionData.getOrDefault(emotion, 0);
            if (value > 0) hasData = true;

            barPaint.setColor(defaultColors[i]);

            float left = padding + (i * (barWidth + spaceWidth));
            float top = height - padding - (value * chartHeight / maxValue);
            float right = left + barWidth;
            float bottom = height - padding - 1;

            barRect.set(left, top, right, bottom);
            canvas.drawRoundRect(barRect, 8f, 8f, barPaint);

            // Draw emotion label
            canvas.drawText(emotion, left + barWidth / 2, height - padding + 30, textPaint);

            // Draw value label if > 0
            if (value > 0) {
                canvas.drawText(String.valueOf(value), left + barWidth / 2, top - 10, valuePaint);
            }
        }

        // Draw "No Data" message if empty
        if (!hasData) {
            textPaint.setTextSize(48f);
            textPaint.setColor(Color.GRAY);
            canvas.drawText("No Data Available", width/2f, height/2f, textPaint);
        }
    }

    public String[] getEmotions() {
        return emotions;
    }
}