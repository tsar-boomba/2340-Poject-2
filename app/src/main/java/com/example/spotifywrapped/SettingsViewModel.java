package com.example.spotifywrapped;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifywrapped.entities.User;

public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>(null);

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.postValue(user);
    }
}