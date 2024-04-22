package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.dolatkia.animatedThemeManager.AppTheme;
import com.dolatkia.animatedThemeManager.ThemeActivity;
import com.dolatkia.animatedThemeManager.ThemeManager;
import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.example.spotifywrapped.spotify.Spotify;
import com.example.spotifywrapped.theme.DarkTheme;
import com.example.spotifywrapped.theme.HolidayTheme;
import com.example.spotifywrapped.theme.LightTheme;
import com.example.spotifywrapped.theme.MyAppTheme;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

public class MainActivity extends ThemeActivity {
    private AppDatabase db;
    private ActivityMainBinding binding;
    private static Spotify spotify = new Spotify();
    private MyAppTheme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = Room.databaseBuilder(this, AppDatabase.class, "db")
                .fallbackToDestructiveMigrationOnDowngrade()
                .fallbackToDestructiveMigration()
                .build();

        // Use for clearing database in development
//        unblock(() -> {
//            db.clearAllTables();
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        spotify.onActivityResult(requestCode, resultCode, data);
    }

    public Spotify getSpotify() {
        return spotify;
    }

    public AppDatabase getDb() {
        return db;
    }

    public void getCode() {
        spotify.getCode(this);
    }

    public void getToken() {
        spotify.getToken(this);
    }

    @Override
    public void onBackPressed() {
        FragmentContainerView fcv = findViewById(R.id.nav_host_fragment_content_main);
        NavHostFragment navHost = fcv.getFragment();
        // TODO: stop navigation back form main frag

        Log.i("MainActivity", "onBackPressed: frag class " + navHost.getChildFragmentManager().findFragmentById(R.id.mainFragment));
        if (fcv.getFragment().getClass() == MainFragment.class) {
            Log.i("MainActivity", "onBackPressed: fragment not null");
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db = null;
        binding = null;
    }

    public Spotify createSpotifyFromToken(String token) {
        spotify = new Spotify();
        spotify.setAccessToken(token);
        return spotify;
    }

    public void setTheme(boolean light, View transitionCenter) {
        getPreferences(MODE_PRIVATE).edit().putBoolean("light", light).apply();
        if (light) {
            theme = new LightTheme();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            ThemeManager.Companion.getInstance().changeTheme(new LightTheme(), transitionCenter, 1600);
        } else {
            theme = new DarkTheme();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            ThemeManager.Companion.getInstance().changeTheme(new DarkTheme(), transitionCenter, 1600);
        }
    }

    public MyAppTheme getCurrentTheme() {
        return theme;
    }

    @NonNull
    @Override
    public AppTheme getStartTheme() {
        boolean lightTheme = getPreferences(MODE_PRIVATE).getBoolean("light", false);

        if (lightTheme) {
            theme = new LightTheme();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return theme;
        } else {
            theme = new DarkTheme();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            return theme;
        }
    }

    @Override
    public void syncTheme(@NonNull AppTheme appTheme) {
        MyAppTheme theme = (MyAppTheme) appTheme;
        binding.getRoot().setBackgroundColor(theme.bgColor(this));
    }
}