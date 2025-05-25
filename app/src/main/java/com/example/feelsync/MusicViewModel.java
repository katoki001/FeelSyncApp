package com.example.feelsync;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MusicViewModel extends ViewModel {
    private final MutableLiveData<Integer> currentSongIndex = new MutableLiveData<>(-1);
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> currentPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isOnlineMusicActive = new MutableLiveData<>(false);

    public LiveData<Integer> getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int index) {
        currentSongIndex.setValue(index);
    }

    public LiveData<Boolean> getIsPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying.setValue(playing);
    }

    public LiveData<Integer> getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        currentPosition.setValue(position);
    }

    public LiveData<Boolean> getIsOnlineMusicActive() {
        return isOnlineMusicActive;
    }

    public void setIsOnlineMusicActive(boolean isActive) {
        isOnlineMusicActive.setValue(isActive);
    }
}