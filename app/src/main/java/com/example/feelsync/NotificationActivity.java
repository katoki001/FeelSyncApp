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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private ListView remindersListView;
    private TextView noRemindersText;
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int EXACT_ALARM_PERMISSION_CODE = 102;
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
        remindersListView = findViewById(R.id.remindersListView);
        noRemindersText = findViewById(R.id.noRemindersText);

        // Load saved reminders
        reminders = loadReminders();
        adapter = new ReminderAdapter(this, reminders);
        remindersListView.setAdapter(adapter);
        updateRemindersVisibility();

        // Restore alarms for loaded reminders
        restoreAlarms();

        // Set reminder button
        findViewById(R.id.setReminderButton).setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Get selected days from checkboxes
            List<Integer> selectedDays = getSelectedDays();
            setReminders(hour, minute, selectedDays);
        });
    }

    private List<Integer> getSelectedDays() {
        List<Integer> selectedDays = new ArrayList<>();
        CheckBox mondayCheckbox = findViewById(R.id.mondayCheckbox);
        CheckBox tuesdayCheckbox = findViewById(R.id.tuesdayCheckbox);
        CheckBox wednesdayCheckbox = findViewById(R.id.wednesdayCheckbox);
        CheckBox thursdayCheckbox = findViewById(R.id.thursdayCheckbox);
        CheckBox fridayCheckbox = findViewById(R.id.fridayCheckbox);
        CheckBox saturdayCheckbox = findViewById(R.id.saturdayCheckbox);
        CheckBox sundayCheckbox = findViewById(R.id.sundayCheckbox);

        if (mondayCheckbox.isChecked()) selectedDays.add(Calendar.MONDAY);
        if (tuesdayCheckbox.isChecked()) selectedDays.add(Calendar.TUESDAY);
        if (wednesdayCheckbox.isChecked()) selectedDays.add(Calendar.WEDNESDAY);
        if (thursdayCheckbox.isChecked()) selectedDays.add(Calendar.THURSDAY);
        if (fridayCheckbox.isChecked()) selectedDays.add(Calendar.FRIDAY);
        if (saturdayCheckbox.isChecked()) selectedDays.add(Calendar.SATURDAY);
        if (sundayCheckbox.isChecked()) selectedDays.add(Calendar.SUNDAY);

        return selectedDays;
    }

    private void setReminders(int hour, int minute, List<Integer> selectedDays) {
        if (selectedDays.isEmpty()) {
            // Default to daily reminders if no days are selected
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                selectedDays.add(i);
            }
        }

        boolean isEveryDay = selectedDays.size() == 7;
        boolean reminderExists = false;

        for (Reminder existingReminder : reminders) {
            if (existingReminder.getHour() == hour && existingReminder.getMinute() == minute) {
                if (isEveryDay && existingReminder.getSelectedDays().size() == 7) {
                    // Update existing "Every day" reminder
                    reminderExists = true;
                    break;
                } else if (!isEveryDay && existingReminder.getSelectedDays().containsAll(selectedDays)) {
                    // Merge selected days into the existing reminder
                    List<Integer> updatedDays = new ArrayList<>(existingReminder.getSelectedDays());
                    updatedDays.addAll(selectedDays);
                    updatedDays = new ArrayList<>(new HashSet<>(updatedDays)); // Remove duplicates

                    if (updatedDays.size() == 7) {
                        updatedDays.clear();
                        updatedDays.addAll(List.of(
                                Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY));
                    }

                    existingReminder.setSelectedDays(updatedDays);
                    reminderExists = true;
                    break;
                }
            }
        }

        if (!reminderExists) {
            // Add a new reminder if no matching time exists
            Reminder newReminder = new Reminder(
                    generateUniqueRequestCode(hour, minute), // Use updated method
                    null, // Scheduled time will be calculated later
                    AlarmManager.INTERVAL_DAY * 7,
                    hour,
                    minute,
                    selectedDays
            );

            // Calculate scheduled time for each day
            for (int day : selectedDays) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }

                long timeInMillis = calendar.getTimeInMillis();
                int requestCode = generateUniqueRequestCode(hour, minute); // Use updated method
                setSingleReminder(timeInMillis, AlarmManager.INTERVAL_DAY * 7, requestCode);

                // Set the first occurrence's time for display purposes
                if (newReminder.getTime() == null) {
                    newReminder.setTime(calendar.getTime());
                }
            }

            reminders.add(newReminder);
        }

        saveReminders(reminders);
        adapter.notifyDataSetChanged();
        updateRemindersVisibility();
        Toast.makeText(this, "Reminders set", Toast.LENGTH_SHORT).show();
    }
    private int generateUniqueRequestCode(int hour, int minute) {
        return (hour * 100) + minute;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void setSingleReminder(long triggerAtMillis, long intervalMillis, int requestCode) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("requestCode", requestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
            );
        }
    }

    private void restoreAlarms() {
        for (Reminder reminder : reminders) {
            for (int day : reminder.getSelectedDays()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, day);
                calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
                calendar.set(Calendar.MINUTE, reminder.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }

                long timeInMillis = calendar.getTimeInMillis();
                setSingleReminder(timeInMillis, reminder.getInterval(), reminder.getId());
            }
        }
    }

    private void cancelExistingAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
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

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        // Remove from list
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getId() == requestCode) {
                reminders.remove(i);
                break;
            }
        }

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
        Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static class Reminder implements Serializable {
        private int id;
        private Date time;
        private long interval;
        private int hour;
        private int minute;
        private List<Integer> selectedDays;

        public Reminder() {
            selectedDays = new ArrayList<>();
        }

        public Reminder(int id, Date time, long interval, int hour, int minute, List<Integer> selectedDays) {
            this.id = id;
            this.time = time;
            this.interval = interval;
            this.hour = hour;
            this.minute = minute;
            this.selectedDays = selectedDays;
        }

        public int getId() {
            return id;
        }

        public Date getTime() {
            return time;
        }

        public long getInterval() {
            return interval;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public List<Integer> getSelectedDays() {
            return selectedDays;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public void setSelectedDays(List<Integer> selectedDays) {
            this.selectedDays = new ArrayList<>(new HashSet<>(selectedDays)); // Remove duplicates
        }
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

            boolean isEveryDay = reminder.getSelectedDays().size() == 7;
            String displayText;
            if (isEveryDay) {
                displayText = "Every day at " + timeString;
            } else {
                StringBuilder daysString = new StringBuilder();
                for (int day : reminder.getSelectedDays()) {
                    switch (day) {
                        case Calendar.MONDAY:
                            daysString.append("Mon ");
                            break;
                        case Calendar.TUESDAY:
                            daysString.append("Tue ");
                            break;
                        case Calendar.WEDNESDAY:
                            daysString.append("Wed ");
                            break;
                        case Calendar.THURSDAY:
                            daysString.append("Thu ");
                            break;
                        case Calendar.FRIDAY:
                            daysString.append("Fri ");
                            break;
                        case Calendar.SATURDAY:
                            daysString.append("Sat ");
                            break;
                        case Calendar.SUNDAY:
                            daysString.append("Sun ");
                            break;
                    }
                }
                displayText = daysString.toString().trim() + " at " + timeString;
            }

            reminderText.setText(displayText);
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
                    .setContentTitle("FeelSync")
                    .setContentText("Hi, how do you feel?")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{200, 500, 200, 500})
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(requestCode, notification);
        }
    }
}