package com.example.feelsync;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.proglish2.R;

public class NotificationActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        timePicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        findViewById(R.id.setReminderButton).setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            setReminder(hour, minute);
        });
    }

    private void setReminder(int hour, int minute) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long timeInMillis = calculateTimeInMillis(hour, minute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show();
    }

    private long calculateTimeInMillis(int hour, int minute) {
        // Set the reminder time to the selected time of the day
        long currentTime = System.currentTimeMillis();
        long targetTime = (hour * 60 * 60 * 1000) + (minute * 60 * 1000);
        long diff = targetTime - currentTime;

        if (diff < 0) {
            // If the target time has passed, set it for the next day
            diff += AlarmManager.INTERVAL_DAY;
        }

        return currentTime + diff;
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @SuppressLint("NotificationPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            Notification notification = new NotificationCompat.Builder(context, "reminder_channel")
                    .setContentTitle("Reminder")
                    .setContentText("It's time to use the app!")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
