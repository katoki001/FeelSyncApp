package com.example.feelsync;


import android.content.Context;
import android.graphics.*;
import android.icu.text.SimpleDateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class EmotionChartView extends View {
    private static final String TAG = "EmotionChartView";
    private Map<String, Integer> emotionData = new HashMap<>();
    private Paint barPaint, textPaint, axisPaint, bgPaint, valuePaint;
    private RectF barRect;
    private int maxValue = 10;
    private String[] emotions = {"Happy", "Angry", "Sad", "Calm", "Lovely"};
    private int[] defaultColors = {
            Color.parseColor("#FFDF55"), // Happy (yellow)
            Color.parseColor("#FF0000"), // Angry (red)
            Color.parseColor("#0000FF"), // Sad (blue)
            Color.parseColor("#00FF00"), // Calm (green)
            Color.parseColor("#FF69B4")  // Lovely (pink)
    };

    private FirebaseFirestore db;
    private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar currentMonth = Calendar.getInstance();

    public EmotionChartView(Context context) {
        super(context);
        init();
    }

    public EmotionChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Background paint
        bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        bgPaint.setStyle(Paint.Style.FILL);

        // Bar paint
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        // Text paint for labels
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#666666"));
        textPaint.setTextSize(36f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Text paint for values
        valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.BLACK);
        valuePaint.setTextSize(32f);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        // Axis paint
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.parseColor("#E0E0E0"));
        axisPaint.setStrokeWidth(3f);

        barRect = new RectF();
    }

    public void loadEmotionDataForMonth(Calendar month) {
        this.currentMonth = (Calendar) month.clone();

        Calendar startCal = (Calendar) currentMonth.clone();
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        String startDate = dbDateFormat.format(startCal.getTime());
        String endDate = dbDateFormat.format(endCal.getTime());

        Log.d(TAG, "Loading data for: " + startDate + " to " + endDate);

        db.collection("notes")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> emotionCounts = new HashMap<>();

                        // Initialize all emotions to 0
                        for (String emotion : emotions) {
                            emotionCounts.put(emotion, 0);
                        }

                        // Count each emotion occurrence
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String emotion = document.getString("emotion");
                            if (emotion != null && emotionCounts.containsKey(emotion)) {
                                emotionCounts.put(emotion, emotionCounts.get(emotion) + 1);
                            }
                        }

                        setEmotionData(emotionCounts);
                    } else {
                        Log.e(TAG, "Error loading data", task.getException());
                        setEmotionData(null); // Will show empty chart
                    }
                });
    }

    public void setEmotionData(Map<String, Integer> data) {
        if (data == null) {
            Log.w(TAG, "Null data received, initializing empty map");
            this.emotionData = new HashMap<>();
        } else {
            this.emotionData = new HashMap<>(data); // Defensive copy
        }

        // Calculate max value (minimum 5 for better visibility)
        this.maxValue = 5;
        for (int value : this.emotionData.values()) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        Log.d(TAG, "New emotion data set. Max value: " + maxValue);
        postInvalidate(); // Ensure UI update on main thread
    }

    public String[] getEmotions() {
        return emotions;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = 500;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height = (heightMode == MeasureSpec.EXACTLY) ? heightSize : Math.min(desiredHeight, heightSize);
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background with rounded corners
        float cornerRadius = 16f;
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius, bgPaint);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        // Draw axes
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint); // Y-axis
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint); // X-axis

        // Draw Y-axis labels and grid lines
        textPaint.setTextSize(24f);
        for (int i = 0; i <= maxValue; i++) {
            float y = height - padding - (i * chartHeight / maxValue);
            canvas.drawText(String.valueOf(i), padding - 40, y + 10, textPaint);
            canvas.drawLine(padding, y, width - padding, y, axisPaint);
        }

        // Calculate bar dimensions
        int barCount = emotions.length;
        float barWidth = (float)chartWidth / (barCount * 2);
        float spaceWidth = barWidth / 2;

        // Draw bars for each emotion
        for (int i = 0; i < barCount; i++) {
            String emotion = emotions[i];
            int value = emotionData.getOrDefault(emotion, 0);

            // Set bar color
            barPaint.setColor(defaultColors[i]);

            // Calculate bar position and dimensions
            float left = padding + (i * (barWidth + spaceWidth)) + spaceWidth;
            float top = height - padding - (value * chartHeight / maxValue);
            float right = left + barWidth;
            float bottom = height - padding - 1;

            // Draw the bar
            barRect.set(left, top, right, bottom);
            canvas.drawRoundRect(barRect, 8f, 8f, barPaint);

            // Draw emotion label below the bar
            canvas.drawText(emotion, left + barWidth/2, height - padding + 40, textPaint);

            // Draw value on top of the bar (if value > 0)
            if (value > 0) {
                canvas.drawText(String.valueOf(value), left + barWidth/2, top - 15, valuePaint);
            }
        }
    }
}