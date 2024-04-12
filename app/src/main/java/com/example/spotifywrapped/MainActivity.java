package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.spotifywrapped.databinding.ActivityMainBinding;
import com.example.spotifywrapped.spotify.Spotify;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private final Spotify spotify = new Spotify();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        db = Room.databaseBuilder(this, AppDatabase.class, "db")
                .fallbackToDestructiveMigrationOnDowngrade()
                .fallbackToDestructiveMigration()
                .build();

        // Use for clearing database in development
//        unblock(() -> {
//            db.clearAllTables();
//        });

//        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    switchCompat.setChecked(true);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("night_mode",true);
//                    editor.apply();
//                }else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    switchCompat.setChecked(false);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("night_mode",false);
//                    editor.apply();
//                }
//            }
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
    }
}