package com.example.spotifywrapped;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifywrapped.entities.Wrapped;

import java.util.Optional;

public class WrappedViewModel extends ViewModel {
    private MutableLiveData<Optional<Wrapped>> wrapped = new MutableLiveData<>();
    private MutableLiveData<Pair<String, String>> message = new MutableLiveData<>();

    public MutableLiveData<Optional<Wrapped>> getWrapped() {
        return wrapped;
    }

    public void setWrapped(Optional<Wrapped> wrapped) {
        this.wrapped.postValue(wrapped);
    }

    public MutableLiveData<Pair<String, String>> getMessage() {
        return message;
    }

    public void setMessage(Pair<String, String> message) {
        this.message.postValue(message);
    }
}