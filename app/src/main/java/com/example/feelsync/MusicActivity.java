package com.example.feelsync;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final String KEY_CURRENT_SONG_INDEX = "current_song_index";
    private static final String KEY_CURRENT_POSITION = "current_position";
    private static final String KEY_IS_PLAYING = "is_playing";

    // UI Components
    private ListView listViewPlaylist;
    private ImageButton btnPlayPause, btnPrevious, btnNext;
    private SeekBar seekBar;
    private TextView txtSongTitle;
    private BottomNavigationView bottomNavigationView;

    // Media Player
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    // Music Data
    private ArrayList<String> musicTitles = new ArrayList<>();
    private ArrayList<Uri> musicUris = new ArrayList<>();
    private int currentSongIndex = -1;
    private MusicViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        // Initialize UI
        initViews();

        // Setup bottom navigation
        setupBottomNavigation();

        // Check and request permissions
        checkAndRequestPermissions();

        // Setup event listeners
        setupEventListeners();

        // Load saved state if available
        if (viewModel.currentSongIndex != -1) {
            restoreFromViewModel();
        }
    }

    private void initViews() {
        listViewPlaylist = findViewById(R.id.list_view_playlist);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seek_bar);
        txtSongTitle = findViewById(R.id.txt_song_title);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_music);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // If already on current activity, do nothing
            if (itemId == R.id.nav_music) {
                return true;
            }

            Class<?> targetActivity = null;
            if (itemId == R.id.nav_calendar) targetActivity = CalendarActivity.class;
            else if (itemId == R.id.nav_ai) targetActivity = AIChatActivity.class;
            else if (itemId == R.id.nav_home) targetActivity = MainPageActivity.class;
            else if (itemId == R.id.nav_statitstics) targetActivity = StatisticsActivity.class;
            else if (itemId == R.id.nav_music) targetActivity = MusicActivity.class;

            if (targetActivity != null) {
                // Clear back stack and prevent multiple instances
                Intent intent = new Intent(this, targetActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        });
    }

    private Class<?> getTargetActivity(int itemId) {
        if (itemId == R.id.nav_calendar) return CalendarActivity.class;
        if (itemId == R.id.nav_ai) return AIChatActivity.class;
        if (itemId == R.id.nav_home) return MainPageActivity.class;
        if (itemId == R.id.nav_statitstics) return StatisticsActivity.class;
        if (itemId == R.id.nav_music) return MusicActivity.class;
        return null;
    }

    private void navigateToActivity(Class<?> targetActivity) {
        // Temporarily disable menu to prevent visual glitches
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Re-enable menu after transition
        new Handler().postDelayed(() -> {
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            bottomNavigationView.setSelectedItemId(getCurrentNavigationItemId(targetActivity));
        }, 300);
    }

    private int getCurrentNavigationItemId(Class<?> targetActivity) {
        if (targetActivity == CalendarActivity.class) return R.id.nav_calendar;
        if (targetActivity == AIChatActivity.class) return R.id.nav_ai;
        if (targetActivity == MainPageActivity.class) return R.id.nav_home;
        if (targetActivity == StatisticsActivity.class) return R.id.nav_statitstics;
        if (targetActivity == MusicActivity.class) return R.id.nav_music;
        return R.id.nav_music; // Default
    }

    private void checkAndRequestPermissions() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_AUDIO :
                Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            showPermissionRationale(permission);
        } else {
            loadMusicFiles();
        }
    }

    private void showPermissionRationale(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to access your music files")
                    .setPositiveButton("OK", (dialog, which) ->
                            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_PERMISSION))
                    .setNegativeButton("Cancel", (dialog, which) ->
                            Toast.makeText(this, "Permission denied. Music playback won't work.", Toast.LENGTH_SHORT).show())
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_PERMISSION);
        }
    }

    private void setupEventListeners() {
        listViewPlaylist.setOnItemClickListener((parent, view, position, id) -> {
            currentSongIndex = position;
            playMusic(currentSongIndex);
        });

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrevious.setOnClickListener(v -> playPrevious());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void restoreFromViewModel() {
        currentSongIndex = viewModel.currentSongIndex;
        musicTitles = viewModel.musicTitles;
        musicUris = viewModel.musicUris;
        updateUIForCurrentSong();

        if (viewModel.isPlaying) {
            playMusic(currentSongIndex);
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(viewModel.currentPosition);
            }
        }
    }

    private void loadMusicFiles() {
        ContentResolver contentResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};

        try (Cursor cursor = contentResolver.query(musicUri, projection,
                MediaStore.Audio.Media.IS_MUSIC + " != 0", null, null)) {

            if (cursor == null || !cursor.moveToFirst()) {
                Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show();
                return;
            }

            musicTitles.clear();
            musicUris.clear();

            int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

            do {
                musicTitles.add(cursor.getString(titleIndex));
                musicUris.add(Uri.parse(cursor.getString(dataIndex)));
            } while (cursor.moveToNext());

            setupPlaylistAdapter();

            // Save to ViewModel
            viewModel.musicTitles = musicTitles;
            viewModel.musicUris = musicUris;
        }
    }

    private void setupPlaylistAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.music_item, android.R.id.text1, musicTitles);
        listViewPlaylist.setAdapter(adapter);
    }

    private void playMusic(int index) {
        if (index < 0 || index >= musicUris.size()) {
            Toast.makeText(this, "Invalid song selection", Toast.LENGTH_SHORT).show();
            return;
        }

        releaseMediaPlayer();

        try {
            mediaPlayer = MediaPlayer.create(this, musicUris.get(index));
            if (mediaPlayer == null) {
                Toast.makeText(this, "Error loading music", Toast.LENGTH_SHORT).show();
                return;
            }

            setupMediaPlayer(index);
            updateUIForCurrentSong();
            updateSeekBar();

            // Save state to ViewModel
            viewModel.currentSongIndex = index;
            viewModel.isPlaying = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error playing music", Toast.LENGTH_SHORT).show();
            Log.e("MusicPlayer", "Error playing music", e);
        }
    }

    private void setupMediaPlayer(int index) {
        mediaPlayer.setOnCompletionListener(mp -> {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            seekBar.setProgress(0);
            playNext();
        });

        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        txtSongTitle.setText(musicTitles.get(index));
        btnPlayPause.setImageResource(R.drawable.ic_pause);
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            if (currentSongIndex != -1) playMusic(currentSongIndex);
            else Toast.makeText(this, "Select a song first", Toast.LENGTH_SHORT).show();
        } else if (mediaPlayer.isPlaying()) {
            pauseMusic();
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            viewModel.isPlaying = true;
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
            viewModel.isPlaying = false;
        }
    }

    private void playNext() {
        if (!musicUris.isEmpty()) {
            currentSongIndex = (currentSongIndex + 1) % musicUris.size();
            playMusic(currentSongIndex);
        }
    }

    private void playPrevious() {
        if (!musicUris.isEmpty()) {
            currentSongIndex = (currentSongIndex - 1 + musicUris.size()) % musicUris.size();
            playMusic(currentSongIndex);
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            viewModel.currentPosition = mediaPlayer.getCurrentPosition();
            handler.postDelayed(this::updateSeekBar, 100);
        }
    }

    private void updateUIForCurrentSong() {
        if (currentSongIndex != -1) {
            txtSongTitle.setText(musicTitles.get(currentSongIndex));
            btnPlayPause.setImageResource(
                    (mediaPlayer != null && mediaPlayer.isPlaying()) ?
                            R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_SONG_INDEX, currentSongIndex);
        if (mediaPlayer != null) {
            outState.putInt(KEY_CURRENT_POSITION, mediaPlayer.getCurrentPosition());
            outState.putBoolean(KEY_IS_PLAYING, mediaPlayer.isPlaying());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentSongIndex = savedInstanceState.getInt(KEY_CURRENT_SONG_INDEX, -1);
        if (currentSongIndex != -1) {
            playMusic(currentSongIndex);
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(savedInstanceState.getInt(KEY_CURRENT_POSITION, 0));
                if (!savedInstanceState.getBoolean(KEY_IS_PLAYING, false)) {
                    pauseMusic();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusicFiles();
            } else {
                disableMusicControls();
            }
        }
    }

    private void disableMusicControls() {
        btnPlayPause.setEnabled(false);
        btnPrevious.setEnabled(false);
        btnNext.setEnabled(false);
        Toast.makeText(this, "Permission denied. Music features disabled.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        // Save final state to ViewModel
        if (mediaPlayer != null) {
            viewModel.currentPosition = mediaPlayer.getCurrentPosition();
        }
    }
}