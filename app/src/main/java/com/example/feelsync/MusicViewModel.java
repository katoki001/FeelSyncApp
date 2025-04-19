package com.example.feelsync;

import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import android.net.Uri;

public class MusicViewModel extends ViewModel {
    public int currentSongIndex = -1;
    public int currentPosition = 0;
    public boolean isPlaying = false;
    public ArrayList<String> musicTitles = new ArrayList<>();
    public ArrayList<Uri> musicUris = new ArrayList<>();
}