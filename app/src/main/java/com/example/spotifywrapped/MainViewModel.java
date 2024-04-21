package com.example.spotifywrapped;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.spotify.AllTopTracks;
import com.example.spotifywrapped.spotify.TopTracks;

import kotlin.Triple;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>(null);
    private final MutableLiveData<AllTopTracks> topTracks = new MutableLiveData<>(new AllTopTracks(null, null, null));

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(@Nullable User user) {
        this.user.postValue(user);
    }

    public LiveData<AllTopTracks> getTopTracks() {
        return topTracks;
    }

    public void setOneMonthTracks(TopTracks topTracks) {
        AllTopTracks currTopTracks = this.topTracks.getValue();
        assert currTopTracks != null;
        currTopTracks.setOneMonthTracks(topTracks);
        this.topTracks.postValue(currTopTracks);
    }

    public void setSixMonthTracks(TopTracks topTracks) {
        AllTopTracks currTopTracks = this.topTracks.getValue();
        assert currTopTracks != null;
        currTopTracks.setSixMonthTracks(topTracks);
        this.topTracks.postValue(currTopTracks);
    }

    public void setOneYearTracks(TopTracks topTracks) {
        AllTopTracks currTopTracks = this.topTracks.getValue();
        assert currTopTracks != null;
        currTopTracks.setOneYearTracks(topTracks);
        this.topTracks.postValue(currTopTracks);
    }
}