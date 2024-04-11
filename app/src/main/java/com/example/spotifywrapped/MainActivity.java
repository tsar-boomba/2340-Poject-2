package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.spotify.Spotify;

public class MainActivity extends AppCompatActivity {
    private final Spotify spotify = new Spotify();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spotify.onActivityResult(requestCode, resultCode, data);
    }

    public Spotify getSpotify() {
        return spotify;
    }

    public void getCode() {
        spotify.getCode(this);
    }

    public void getToken() {
        spotify.getToken(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}