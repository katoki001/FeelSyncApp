package com.example.feelsync;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.proglish2.R;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {
    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    private String currentSongUrl;
    private boolean isPrepared = false;

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if ("PLAY".equals(action)) {
                String url = intent.getStringExtra("URL");
                if (url != null && !url.equals(currentSongUrl)) {
                    currentSongUrl = url;
                    startPlayback(url);
                } else if (isPrepared) {
                    mediaPlayer.start();
                    updateNotification(true);
                }
            } else if ("PAUSE".equals(action)) {
                pausePlayback();
            } else if ("STOP".equals(action)) {
                stopPlayback();
            } else if ("TOGGLE".equals(action)) {
                if (mediaPlayer.isPlaying()) {
                    pausePlayback();
                } else if (isPrepared) {
                    mediaPlayer.start();
                    updateNotification(true);
                }
            }
        }
        return START_STICKY;
    }

    private void startPlayback(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            isPrepared = false;
        } catch (IOException e) {
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void pausePlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification(false);
        }
    }

    private void stopPlayback() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        stopForeground(true);
        stopSelf();
        isPrepared = false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        mp.start();
        startForeground(NOTIFICATION_ID, buildNotification(true));
    }

    private Notification buildNotification(boolean isPlaying) {
        Intent notificationIntent = new Intent(this, MusicActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction("PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(this,
                0, playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(this,
                0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, MusicService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(this,
                0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText("Now Playing")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentIntent(pendingIntent)
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Pause" : "Play",
                        isPlaying ? pausePendingIntent : playPendingIntent)
                .addAction(R.drawable.ic_pause, "Stop", stopPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification(boolean isPlaying) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, buildNotification(isPlaying));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void playSong(String url) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("PLAY");
        intent.putExtra("URL", url);
        startService(intent);
    }

    public void togglePlayback() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("TOGGLE");
        startService(intent);
    }
}