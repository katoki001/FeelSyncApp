package com.example.feelsync;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DayView extends View {
    private int day;
    private List<String> emotions;
    private boolean isSelected;
    private Paint paint;
    private RectF rectF;

    public DayView(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    public void setDate(int day, List<String> emotions) {
        if (this.day != day || !Objects.equals(this.emotions, emotions)) {
            this.day = day;
            this.emotions = emotions != null ? new ArrayList<>(emotions) : null;
            invalidate();  // Correct way to trigger redraw
        }
    }

    public void setSelected(boolean selected) {
        if (isSelected != selected) {
            isSelected = selected;
            invalidate();  // Correct way to trigger redraw
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 10;
        int centerX = width / 2;
        int centerY = height / 2;

        // Draw selection background
        if (isSelected) {
            paint.setColor(Color.parseColor("#88A24C"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX, centerY, radius + 5, paint);
        }

        // Draw emotion segments
        if (emotions != null && !emotions.isEmpty()) {
            float startAngle = -90; // Start at top
            float sweepAngle = 360f / emotions.size();
            rectF.set(centerX - radius, centerY - radius,
                    centerX + radius, centerY + radius);

            for (String emotion : emotions) {
                paint.setColor(getEmotionColor(emotion));
                canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
                startAngle += sweepAngle;
            }
        }

        // Draw day number
        paint.setColor(isSelected ? Color.WHITE : Color.BLACK);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(day), centerX, centerY + 10, paint);
    }

    private int getEmotionColor(String emotion) {
        // Replace this with your actual color management
        switch (emotion) {
            case "Happy": return Color.YELLOW;
            case "Angry": return Color.RED;
            case "Sad": return Color.BLUE;
            case "Calm": return Color.GREEN;
            case "Love": return Color.MAGENTA;
            default: return Color.GRAY;
        }
    }
}