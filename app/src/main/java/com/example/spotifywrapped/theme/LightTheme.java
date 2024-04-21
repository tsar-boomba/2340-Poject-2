package com.example.spotifywrapped.theme;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.spotifywrapped.R;

import org.jetbrains.annotations.NotNull;

public class LightTheme implements MyAppTheme {
    public int id() { // set unique iD for each theme
        return 0;
    }

    public int bgColor(@NotNull Context context) {
        return ContextCompat.getColor(context, R.color.bg_light);
    }

    public int textColor(@NotNull Context context) {
        return ContextCompat.getColor(context,  R.color.text_light);
    }

    public int buttonColor(@NotNull Context context) {
        return ContextCompat.getColor(context, R.color.button_light);
    }
}
