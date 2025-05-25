package com.example.feelsync;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isOnlineFragmentActive = new MutableLiveData<>(false);

    public LiveData<Boolean> isOnlineFragmentActive() {
        return isOnlineFragmentActive;
    }

    public void setOnlineFragmentActive(boolean isActive) {
        isOnlineFragmentActive.setValue(isActive);
    }
}