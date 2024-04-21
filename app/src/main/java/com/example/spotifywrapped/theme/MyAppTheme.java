package com.example.spotifywrapped.theme;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dolatkia.animatedThemeManager.AppTheme;

public interface MyAppTheme extends AppTheme {
    int textColor(@NonNull Context context);
    int bgColor(@NonNull Context context);
    int buttonColor(@NonNull Context context);
}
