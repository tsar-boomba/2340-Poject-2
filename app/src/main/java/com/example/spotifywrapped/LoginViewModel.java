package com.example.spotifywrapped;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(true);

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading.postValue(loading);
    }
}