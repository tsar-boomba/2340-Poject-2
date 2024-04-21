package com.example.spotifywrapped.theme;


import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.spotifywrapped.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.Optional;

public class HolidayTheme implements MyAppTheme {
    private Color backgroundColor1;
    private Color backgroundColor2;
    private Color textColor1;
    private Color textColor2;

    @Override
    public int id() {
        return 2;
    }

    private HolidayTheme(Color backgroundColor1, Color backgroundColor2, Color textColor1, Color textColor2) {
        this.backgroundColor1 = backgroundColor1;
        this.backgroundColor2 = backgroundColor2;
        this.textColor1 = textColor1;
        this.textColor2 = textColor2;
    }

    public static Optional<HolidayTheme> getHolidayTheme(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        if (date.getMonth() == Month.DECEMBER && date.getDayOfMonth() == 25) {
            return Optional.of(christmas());
        } else if (date.getMonth() == Month.MARCH && date.getDayOfMonth() == 17) {
            return Optional.of(stPattys());
        } else if (date.getMonth() == Month.JULY && date.getDayOfMonth() == 4) {
            return Optional.of(fourthOfJuly());
        } else if (date.getMonth() == Month.NOVEMBER && date.getDayOfMonth() == 28) {
            return Optional.of(thanksgiving());
        }
        return Optional.empty();
    }

    private static HolidayTheme christmas() {
        return new HolidayTheme(
                Color.valueOf(Color.rgb(204, 184, 142)),
                Color.valueOf(Color.rgb(179, 150, 86)),
                Color.valueOf(Color.rgb(155, 8, 28)),
                Color.valueOf(Color.rgb(5, 50, 37))
        );
    }

    private static HolidayTheme stPattys() {
        return new HolidayTheme(
                Color.valueOf(Color.rgb(0, 156, 26)),
                Color.valueOf(Color.rgb(53, 219, 80)),
                Color.valueOf(Color.rgb(240, 169, 2)),
                Color.valueOf(Color.rgb(255, 136, 0))
        );
    }

    private static HolidayTheme fourthOfJuly() {
        return new HolidayTheme(
                Color.valueOf(Color.rgb(0, 55, 184)),
                Color.valueOf(Color.rgb(196, 23, 0)),
                Color.valueOf(Color.rgb(255, 255, 255)),
                Color.valueOf(Color.rgb(186, 221, 255))
        );
    }

    private static HolidayTheme thanksgiving() {
        return new HolidayTheme(
                Color.valueOf(Color.rgb(235, 196, 0)),
                Color.valueOf(Color.rgb(71, 58, 6)),
                Color.valueOf(Color.rgb(255, 166, 0)),
                Color.valueOf(Color.rgb(224, 158, 43))
        );
    }

    public int bgColor(@NonNull Context context) {
        return backgroundColor1.toArgb();
    }

    public int textColor(@NonNull Context context) {
        return textColor1.toArgb();
    }

    public int buttonColor(@NonNull Context context) {
        return ContextCompat.getColor(context, R.color.button_light);
    }
}
