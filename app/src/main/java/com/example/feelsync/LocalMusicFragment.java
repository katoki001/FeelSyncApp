package com.example.feelsync;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proglish2.R;
import com.google.firebase.database.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalMusicFragment extends Fragment {
    private static final String TAG = "LocalMusicFragment";
    private static final int SEEK_BAR_UPDATE_DELAY_MS = 100;
    private static final int PRELOAD_NEXT_SONG_COUNT = 2;

    // UI Components
    private RecyclerView recyclerView;
    private ImageButton btnPlayPause, btnPrevious, btnNext;
    private SeekBar seekBar;
    private TextView txtSongTitle, txtSongArtist;

    // ViewModels
    private SharedViewModel sharedViewModel;
    private MusicViewModel viewModel;

    // Media Player
    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isPreparing = false;

    // Data
    private final List<SongLocal> musicList = new ArrayList<>();
    private int currentSongIndex = -1;
    private boolean shouldRestorePlayback = true;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadLocalMusic();
                } else {
                    showPermissionDeniedMessage();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retain fragment instance across configuration changes
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        initViews(view);
        initViewModels();
        checkPermissionsAndLoadMusic();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        seekBar = view.findViewById(R.id.seek_bar);
        txtSongTitle = view.findViewById(R.id.txt_song_title);
        txtSongArtist = view.findViewById(R.id.txt_song_artist);

        txtSongTitle.setText(R.string.no_song_selected);
        txtSongArtist.setText("");

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrevious.setOnClickListener(v -> playPrevious());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && !isPreparing) {
                    try {
                        mediaPlayer.seekTo(progress);
                        viewModel.setCurrentPosition(progress);
                        seekBar.setProgress(progress);
                        updateSeekBar();
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "Error in onProgressChanged", e);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe changes in the current song index
        viewModel.getCurrentSongIndex().observe(getViewLifecycleOwner(), index -> {
            if (index != null && index != -1 && index < musicList.size() && index != currentSongIndex) {
                currentSongIndex = index;
                if (shouldRestorePlayback) {
                    playMusic(index);
                }
            }
        });

        // Pause local music playback when the online fragment becomes active
        sharedViewModel.isOnlineFragmentActive().observe(getViewLifecycleOwner(), isActive -> {
            if (isActive != null && isActive && mediaPlayer != null && mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.pause();
                    viewModel.setPlaying(false);
                    handler.removeCallbacksAndMessages(null);
                    updatePlayPauseIcon(false);
                } catch (IllegalStateException e) {
                    Log.e(TAG, "Error pausing media player in SharedViewModel observer", e);
                }
            }
        });
    }

    private void checkPermissionsAndLoadMusic() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            loadLocalMusic();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(requireContext(),
                R.string.permission_required_message,
                Toast.LENGTH_LONG).show();
    }

    private void loadLocalMusic() {
        executor.execute(() -> {
            musicList.clear();
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID
            };
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

            try (Cursor cursor = requireContext().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder)) {

                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                    int albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

                    while (cursor.moveToNext()) {
                        try {
                            String path = cursor.getString(pathColumn);
                            if (path != null && new File(path).exists()) {
                                musicList.add(new SongLocal(
                                        cursor.getLong(idColumn),
                                        cursor.getString(titleColumn),
                                        cursor.getString(artistColumn),
                                        path,
                                        cursor.getLong(durationColumn),
                                        cursor.getString(albumColumn),
                                        cursor.getLong(albumIdColumn)
                                ));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading music file entry", e);
                        }
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    if (musicList.isEmpty()) {
                        showNoMusicFoundMessage();
                    } else {
                        setupRecyclerView();
                        restorePlaybackState();
                        preloadNextSongs(0);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading music", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.error_loading_music, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void preloadNextSongs(int currentIndex) {
        int endIndex = Math.min(currentIndex + PRELOAD_NEXT_SONG_COUNT, musicList.size() - 1);
        for (int i = currentIndex + 1; i <= endIndex; i++) {
            preloadSongMetadata(i);
        }
    }

    private void preloadSongMetadata(int position) {
        executor.execute(() -> {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(musicList.get(position).getPath());
                retriever.release();
            } catch (Exception e) {
                Log.w(TAG, "Failed to preload metadata for song at position " + position);
            }
        });
    }

    private void showNoMusicFoundMessage() {
        Toast.makeText(requireContext(),
                R.string.no_music_found_message,
                Toast.LENGTH_LONG).show();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        MusicAdapterLocal adapter = new MusicAdapterLocal(musicList, position -> {
            if (!isPreparing) {
                currentSongIndex = position;
                playMusic(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void restorePlaybackState() {
        if (viewModel.getCurrentSongIndex().getValue() != null &&
                viewModel.getCurrentSongIndex().getValue() != -1) {
            currentSongIndex = viewModel.getCurrentSongIndex().getValue();
            int savedPosition = viewModel.getCurrentPosition().getValue() != null
                    ? viewModel.getCurrentPosition().getValue()
                    : 0;
            boolean isPlaying = viewModel.getIsPlaying().getValue() != null
                    ? viewModel.getIsPlaying().getValue()
                    : false;

            if (isPlaying) {
                if (mediaPlayer == null) {
                    playMusic(currentSongIndex, savedPosition);
                } else {
                    mediaPlayer.seekTo(savedPosition);
                    mediaPlayer.start();
                    updatePlayPauseIcon(true);
                    updateSeekBar();
                }
            } else {
                updateUIForCurrentSong();
            }
        }
    }

    private void playMusic(int index, int startPosition) {
        if (index < 0 || index >= musicList.size() || isPreparing) return;

        executor.execute(() -> {
            try {
                isPreparing = true;
                SongLocal song = musicList.get(index);
                releaseMediaPlayer();

                MediaPlayer tempPlayer = new MediaPlayer();
                tempPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
                tempPlayer.setDataSource(song.getPath());
                tempPlayer.prepareAsync();

                tempPlayer.setOnPreparedListener(mp -> {
                    try {
                        mediaPlayer = tempPlayer;
                        isPreparing = false;
                        mp.seekTo(startPosition);
                        mp.start();

                        requireActivity().runOnUiThread(() -> {
                            seekBar.setMax(mp.getDuration());
                            updatePlayPauseIcon(true);
                            updateUIForCurrentSong();
                            updateSeekBar();
                        });

                        viewModel.setCurrentSongIndex(index);
                        viewModel.setPlaying(true);
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "MediaPlayer error in onPrepared", e);
                        isPreparing = false;
                    }
                });

                tempPlayer.setOnCompletionListener(mp -> playNext());
                tempPlayer.setOnErrorListener((mp, what, extra) -> {
                    isPreparing = false;
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), R.string.playback_error, Toast.LENGTH_SHORT).show());
                    return true;
                });
            } catch (IOException | IllegalStateException e) {
                isPreparing = false;
                Log.e(TAG, "Error initializing MediaPlayer", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), R.string.error_playing_song, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void togglePlayPause() {
        if (isPreparing) return;

        if (mediaPlayer == null) {
            if (currentSongIndex != -1) {
                playMusic(currentSongIndex);
            } else if (!musicList.isEmpty()) {
                playMusic(0);
            }
        } else if (mediaPlayer.isPlaying()) {
            pauseMusic();
        } else {
            resumeMusic();
        }
    }

    private void playMusic(int currentSongIndex) {
        playMusic(currentSongIndex, 0);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                updatePlayPauseIcon(false);
                viewModel.setPlaying(false);
                handler.removeCallbacksAndMessages(null);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error pausing media player", e);
            }
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
                updatePlayPauseIcon(true);
                viewModel.setPlaying(true);
                updateSeekBar();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error resuming media player", e);
            }
        }
    }

    private void playNext() {
        if (musicList.isEmpty() || isPreparing) return;
        currentSongIndex = (currentSongIndex + 1) % musicList.size();
        playMusic(currentSongIndex);
        preloadNextSongs(currentSongIndex);
    }

    private void playPrevious() {
        if (musicList.isEmpty() || isPreparing) return;
        currentSongIndex = (currentSongIndex - 1 + musicList.size()) % musicList.size();
        playMusic(currentSongIndex);
        preloadNextSongs(currentSongIndex);
    }

    private void updateSeekBar() {
        try {
            if (mediaPlayer != null && !isPreparing) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                viewModel.setCurrentPosition(currentPosition);
                handler.postDelayed(this::updateSeekBar, SEEK_BAR_UPDATE_DELAY_MS);
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "MediaPlayer in illegal state during seekbar update", e);
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void updateUIForCurrentSong() {
        if (currentSongIndex != -1 && currentSongIndex < musicList.size()) {
            SongLocal currentSong = musicList.get(currentSongIndex);
            txtSongTitle.setText(currentSong.getTitle());
            txtSongArtist.setText(currentSong.getArtist() != null ?
                    currentSong.getArtist() : getString(R.string.unknown_artist));
            recyclerView.smoothScrollToPosition(currentSongIndex);
        }
    }

    private void updatePlayPauseIcon(boolean isPlaying) {
        btnPlayPause.setImageResource(isPlaying ?
                R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error releasing MediaPlayer", e);
            } finally {
                mediaPlayer = null;
                handler.removeCallbacksAndMessages(null);
                isPreparing = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            try {
                viewModel.setCurrentPosition(mediaPlayer.getCurrentPosition());
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    viewModel.setPlaying(false);
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error in onPause", e);
            }
        }
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null &&
                viewModel.getIsPlaying().getValue() != null &&
                viewModel.getIsPlaying().getValue()) {
            try {
                mediaPlayer.start();
                int savedPosition = viewModel.getCurrentPosition().getValue() != null
                        ? viewModel.getCurrentPosition().getValue()
                        : 0;
                mediaPlayer.seekTo(savedPosition);
                updateSeekBar();
                updatePlayPauseIcon(true);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error in onResume", e);
            }
        }
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        handler.removeCallbacksAndMessages(null);
        executor.shutdownNow();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Pause MediaPlayer when leaving the fragment
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                viewModel.setPlaying(false);
                handler.removeCallbacksAndMessages(null);
                updatePlayPauseIcon(false);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error pausing media player in onStop", e);
            }
        }
    }
}