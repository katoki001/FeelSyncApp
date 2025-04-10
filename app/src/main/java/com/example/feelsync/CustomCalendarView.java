package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.proglish2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomCalendarView extends LinearLayout {
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private Map<String, List<String>> dateEmotionsMap = new HashMap<>();
    private String selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Calendar currentMonth = Calendar.getInstance();
    private OnDateSelectedListener listener;
    private SharedPreferences colorPreferences;
    private TextView monthYearTextView;
    private ImageButton prevMonthButton, nextMonthButton;

    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        colorPreferences = getContext().getSharedPreferences("ColorPreferences", Context.MODE_PRIVATE);

        // Inflate the header layout
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_header, this, true);

        // Initialize views
        monthYearTextView = findViewById(R.id.monthYearText);
        prevMonthButton = findViewById(R.id.prevMonthButton);
        nextMonthButton = findViewById(R.id.nextMonthButton);

        // Set up month navigation
        prevMonthButton.setOnClickListener(v -> navigateMonth(-1));
        nextMonthButton.setOnClickListener(v -> navigateMonth(1));

        updateCalendar();
    }

    private void navigateMonth(int monthsToAdd) {
        currentMonth.add(Calendar.MONTH, monthsToAdd);
        updateCalendar();
        if (listener != null) {
            listener.onMonthChanged(currentMonth);
        }
    }

    public Calendar getCurrentMonth() {
        return (Calendar) currentMonth.clone();
    }

    public void setDateEmotionsMap(Map<String, List<String>> dateEmotionsMap) {
        this.dateEmotionsMap = new HashMap<>(dateEmotionsMap); // Create new instance
        invalidate(); // Force redraw
    }

    public void setSelectedDate(String date) {
        this.selectedDate = date;
        updateCalendar();
    }

    public void setCurrentMonth(Calendar calendar) {
        this.currentMonth = (Calendar) calendar.clone();
        updateCalendar();
    }

    private void updateCalendar() {
        // Remove all views except the header (first child)
        if (getChildCount() > 1) {
            removeViews(1, getChildCount() - 1);
        }

        // Update month/year text
        updateMonthYearText();

        // Add day headers
        addDayHeaders();

        // Generate calendar days
        generateCalendarDays();
    }

    private void updateMonthYearText() {
        String monthYear = new SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                .format(currentMonth.getTime());
        monthYearTextView.setText(monthYear);
    }

    public void updateEmotionCircles(List<Note> notes) {
        dateEmotionsMap.clear();
        for (Note note : notes) {
            if (!dateEmotionsMap.containsKey(note.date)) {
                dateEmotionsMap.put(note.date, new ArrayList<>());
            }
            dateEmotionsMap.get(note.date).add(note.emotion);
        }
        invalidate(); // Redraw the view
    }

    private void addDayHeaders() {
        LinearLayout headerLayout = new LinearLayout(getContext());
        headerLayout.setOrientation(HORIZONTAL);
        headerLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (String day : DAYS) {
            TextView dayView = new TextView(getContext());
            dayView.setText(day);
            dayView.setLayoutParams(new LayoutParams(
                    0, LayoutParams.WRAP_CONTENT, 1f));
            dayView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            dayView.setTextColor(Color.parseColor("#666666"));
            headerLayout.addView(dayView);
        }
        addView(headerLayout);
    }

    private void generateCalendarDays() {
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int weeks = (int) Math.ceil((firstDayOfWeek - 1 + daysInMonth) / 7.0);

        int dayCounter = 1;
        for (int i = 0; i < weeks; i++) {
            LinearLayout weekLayout = new LinearLayout(getContext());
            weekLayout.setOrientation(HORIZONTAL);
            weekLayout.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < 7; j++) {
                if ((i == 0 && j < firstDayOfWeek - 1) || dayCounter > daysInMonth) {
                    addEmptyDayCell(weekLayout);
                } else {
                    addDayCell(weekLayout, calendar, dayCounter);
                    dayCounter++;
                }
            }
            addView(weekLayout);
        }
    }

    private void addEmptyDayCell(LinearLayout weekLayout) {
        View emptyView = new View(getContext());
        emptyView.setLayoutParams(new LayoutParams(0, 150, 1f));
        weekLayout.addView(emptyView);
    }

    private void addDayCell(LinearLayout weekLayout, Calendar calendar, int day) {
        Calendar dayCalendar = (Calendar) calendar.clone();
        dayCalendar.set(Calendar.DAY_OF_MONTH, day);
        String date = dateFormat.format(dayCalendar.getTime());

        DayView dayView = new DayView(getContext());
        dayView.setDate(day, dateEmotionsMap.getOrDefault(date, new ArrayList<>()));
        dayView.setSelected(date.equals(selectedDate));
        dayView.setToday(isToday(dayCalendar));
        dayView.setLayoutParams(new LayoutParams(0, 150, 1f));

        dayView.setOnClickListener(v -> {
            selectedDate = date;
            if (listener != null) {
                listener.onDateSelected(date);
            }
            updateCalendar();
        });

        weekLayout.addView(dayView);
    }

    private boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
        default void onMonthChanged(Calendar newMonth) {}
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    private class DayView extends View {
        private int day;
        private List<String> emotions;
        private boolean isSelected;
        private boolean isToday;
        private Paint paint;
        private RectF rectF;

        public DayView(Context context) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            rectF = new RectF();
        }

        public void setDate(int day, List<String> emotions) {
            this.day = day;
            this.emotions = new ArrayList<>(emotions); // Defensive copy
            invalidate();
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
            invalidate();
        }

        public void setToday(boolean today) {
            isToday = today;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();
            int centerX = width / 2;
            int centerY = height / 2;
            int radius = Math.min(width, height) / 2;

            drawTodayIndicator(canvas, centerX, centerY, radius);
            drawSelectedBackground(canvas, centerX, centerY, radius);
            drawDayNumber(canvas, centerX, centerY);
            drawEmotionIndicators(canvas, centerX, centerY, radius);
        }

        private void drawTodayIndicator(Canvas canvas, int centerX, int centerY, int radius) {
            if (isToday && !isSelected) {
                paint.setColor(Color.parseColor("#88A24C"));
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                canvas.drawCircle(centerX, centerY, radius - 10, paint);
            }
        }

        private void drawSelectedBackground(Canvas canvas, int centerX, int centerY, int radius) {
            if (isSelected) {
                paint.setColor(Color.parseColor("#88A24C"));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX, centerY, radius - 5, paint);
            }
        }

        private void drawDayNumber(Canvas canvas, int centerX, int centerY) {
            paint.setColor(isSelected ? Color.WHITE : (isToday ? Color.parseColor("#88A24C") : Color.BLACK));
            paint.setTextSize(30);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(day), centerX, centerY + 10, paint);
        }

        private void drawEmotionIndicators(Canvas canvas, int centerX, int centerY, int radius) {
            if (!emotions.isEmpty()) {
                float startAngle = 0;
                float sweepAngle = 360f / emotions.size();
                int emotionRadius = radius - 20;
                int ringWidth = 12;

                for (String emotion : emotions) {
                    paint.setColor(getColorForEmotion(emotion));
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(ringWidth);
                    rectF.set(centerX - emotionRadius, centerY - emotionRadius,
                            centerX + emotionRadius, centerY + emotionRadius);
                    canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
                    startAngle += sweepAngle;
                }
            }
        }

        private int getColorForEmotion(String emotion) {
            switch (emotion) {
                case "Happy": return getSavedColor("colorHappy");
                case "Angry": return getSavedColor("colorAngry");
                case "Sad": return getSavedColor("colorSad");
                case "Calm": return getSavedColor("colorCalm");
                case "Love": return getSavedColor("colorLove");
                default: return Color.GRAY;
            }
        }

        private int getSavedColor(String colorKey) {
            int red = colorPreferences.getInt(colorKey + "_red", -1);
            if (red == -1) {
                switch (colorKey) {
                    case "colorHappy": return Color.YELLOW;
                    case "colorAngry": return Color.RED;
                    case "colorSad": return Color.BLUE;
                    case "colorCalm": return Color.GREEN;
                    case "colorLove": return Color.MAGENTA;
                    default: return Color.GRAY;
                }
            }
            int green = colorPreferences.getInt(colorKey + "_green", 0);
            int blue = colorPreferences.getInt(colorKey + "_blue", 0);
            return Color.rgb(red, green, blue);
        }
    }
}