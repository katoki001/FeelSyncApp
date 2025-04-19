package com.example.feelsync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;

import com.example.proglish2.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Spinner frequencySpinner;
    private AlarmManager alarmManager;
    private ListView remindersListView;
    private TextView noRemindersText;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int EXACT_ALARM_PERMISSION_CODE = 102;
    private int selectedFrequency = 1;
    private List<Reminder> reminders = new ArrayList<>();
    private ReminderAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        checkAndRequestNotificationPermission();
        createNotificationChannel();

        timePicker = findViewById(R.id.timePicker);
        frequencySpinner = findViewById(R.id.frequencySpinner);
        remindersListView = findViewById(R.id.remindersListView);
        noRemindersText = findViewById(R.id.noRemindersText);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Setup frequency spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(spinnerAdapter);

        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFrequency = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFrequency = 1;
            }
        });

        // Load saved reminders
        reminders = loadReminders();
        adapter = new ReminderAdapter(this, reminders);
        remindersListView.setAdapter(adapter);
        updateRemindersVisibility();

        // Restore alarms for loaded reminders
        restoreAlarms();

        findViewById(R.id.setReminderButton).setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            setReminders(hour, minute);
        });
    }

    private void setReminders(int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            requestExactAlarmPermission();
            return;
        }

        cancelExistingAlarms();
        reminders.clear();

        // Calculate time between reminders in minutes
        int minutesBetweenReminders = (24 * 60) / selectedFrequency;

        for (int i = 0; i < selectedFrequency; i++) {
            // Calculate offset minutes for this reminder
            int offsetMinutes = i * minutesBetweenReminders;

            // Calculate specific hour and minute for this reminder
            int reminderHour = hour + (offsetMinutes / 60);
            int reminderMinute = minute + (offsetMinutes % 60);

            // Normalize the time (handle overflow)
            reminderHour = reminderHour % 24;
            reminderMinute = reminderMinute % 60;

            // Calculate the time in millis
            long timeInMillis = calculateTimeInMillis(reminderHour, reminderMinute, 0, 0);

            // Use a unique request code for each reminder
            int requestCode = generateUniqueRequestCode(reminderHour, reminderMinute);

            setSingleReminder(timeInMillis, AlarmManager.INTERVAL_DAY, requestCode);

            // Add to reminders list
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            reminders.add(new Reminder(requestCode, calendar.getTime(), AlarmManager.INTERVAL_DAY, reminderHour, reminderMinute));
        }

        // Save reminders
        saveReminders(reminders);

        adapter.notifyDataSetChanged();
        updateRemindersVisibility();
        Toast.makeText(this, selectedFrequency + " reminders set", Toast.LENGTH_SHORT).show();
    }

    private int generateUniqueRequestCode(int hour, int minute) {
        return (hour * 100) + minute;
    }

    private void setSingleReminder(long triggerAtMillis, long intervalMillis, int requestCode) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("requestCode", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Log.d("Reminder", "Setting alarm #" + requestCode + " for: " + new Date(triggerAtMillis));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        }
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivityForResult(intent, EXACT_ALARM_PERMISSION_CODE);
        }
    }

    private void restoreAlarms() {
        for (Reminder reminder : reminders) {
            long timeInMillis = calculateTimeInMillis(reminder.getHour(), reminder.getMinute(), 0, 0);
            setSingleReminder(timeInMillis, reminder.getInterval(), reminder.getId());
        }
    }

    private void cancelExistingAlarms() {
        for (Reminder reminder : reminders) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    reminder.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    private void cancelReminder(int requestCode) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        // Remove from list
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getId() == requestCode) {
                reminders.remove(i);
                break;
            }
        }

        // Save updated reminders
        saveReminders(reminders);

        adapter.notifyDataSetChanged();
        updateRemindersVisibility();
        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
    }

    private void updateRemindersVisibility() {
        if (reminders.isEmpty()) {
            noRemindersText.setVisibility(View.VISIBLE);
            remindersListView.setVisibility(View.GONE);
        } else {
            noRemindersText.setVisibility(View.GONE);
            remindersListView.setVisibility(View.VISIBLE);
        }
    }

    private long calculateTimeInMillis(int baseHour, int baseMinute, int iteration, long intervalMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, baseHour);
        calendar.set(Calendar.MINUTE, baseMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long timeInMillis = calendar.getTimeInMillis();

        if (timeInMillis <= System.currentTimeMillis()) {
            timeInMillis += AlarmManager.INTERVAL_DAY;
        }

        return timeInMillis;
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for reminder notifications");
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void saveReminders(List<Reminder> reminders) {
        SharedPreferences preferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(reminders);

        editor.putString("reminders", json);
        editor.apply();
    }

    private List<Reminder> loadReminders() {
        SharedPreferences preferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        String json = preferences.getString("reminders", null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Reminder>>(){}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXACT_ALARM_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Exact alarm permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exact alarm permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class Reminder implements Serializable {
        private int id;
        private Date time;
        private long interval;
        private int hour;
        private int minute;

        public Reminder() {
        }

        public Reminder(int id, Date time, long interval, int hour, int minute) {
            this.id = id;
            this.time = time;
            this.interval = interval;
            this.hour = hour;
            this.minute = minute;
        }

        public int getId() { return id; }
        public Date getTime() { return time; }
        public long getInterval() { return interval; }
        public int getHour() { return hour; }
        public int getMinute() { return minute; }

        public void setId(int id) { this.id = id; }
        public void setTime(Date time) { this.time = time; }
        public void setInterval(long interval) { this.interval = interval; }
        public void setHour(int hour) { this.hour = hour; }
        public void setMinute(int minute) { this.minute = minute; }
    }

    private class ReminderAdapter extends ArrayAdapter<Reminder> {
        public ReminderAdapter(Context context, List<Reminder> reminders) {
            super(context, 0, reminders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Reminder reminder = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.reminder_item, parent, false);
            }

            TextView reminderText = convertView.findViewById(R.id.reminderText);
            Button deleteButton = convertView.findViewById(R.id.deleteButton);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = sdf.format(reminder.getTime());

            reminderText.setText("Daily at " + timeString);

            deleteButton.setOnClickListener(v -> cancelReminder(reminder.getId()));

            return convertView;
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int requestCode = intent.getIntExtra("requestCode", 0);
            Log.d("NotificationReceiver", "Received alarm for requestCode: " + requestCode);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (notificationManager.areNotificationsEnabled()) {
                    showNotification(context, requestCode);
                } else {
                    Log.e("NotificationReceiver", "Notification permission not granted");
                }
            } else {
                showNotification(context, requestCode);
            }
        }

        private void showNotification(Context context, int requestCode) {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Reminder #" + (requestCode + 1))
                    .setContentText("It's time to use the app!")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 200, 500})
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(requestCode, notification);
        }
    }
}