package com.example.spotifywrapped.theme;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.spotifywrapped.R;

import org.jetbrains.annotations.NotNull;

public class DarkTheme implements MyAppTheme {
    public int id() { // set unique iD for each theme
        return 1;
    }

    public int bgColor(@NotNull Context context) {
        return ContextCompat.getColor(context, R.color.bg_dark);
    }

    public int textColor(@NotNull Context context) {
        return ContextCompat.getColor(context,  R.color.text_dark);
    }

    public int buttonColor(@NotNull Context context) {
        return ContextCompat.getColor(context, R.color.button_dark);
    }
}
