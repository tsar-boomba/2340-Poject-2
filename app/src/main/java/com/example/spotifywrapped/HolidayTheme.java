package com.example.spotifywrapped;


import android.graphics.Color;

import java.util.Date;
import java.util.Optional;

public class HolidayTheme {
    private Color backgroundColor1;
    private Color backgroundColor2;
    private Color textColor1;
    private Color textColor2;

    private HolidayTheme(String holiday) {
        if (holiday.equals("christmas")) {
            backgroundColor1 = Color.valueOf(Color.rgb(204, 184, 142));
            backgroundColor2 = Color.valueOf(Color.rgb(179, 150, 86));
            textColor1 = Color.valueOf(Color.rgb(155, 8, 28));
            textColor2 = Color.valueOf(Color.rgb(5, 50, 37));
        } else if (holiday.equals("st pattys")) {
            backgroundColor1 = Color.valueOf(Color.rgb(0, 156, 26));
            backgroundColor2 = Color.valueOf(Color.rgb(53, 219, 80));
            textColor1 = Color.valueOf(Color.rgb(240, 169, 2));
            textColor2 = Color.valueOf(Color.rgb(255, 136, 0));
        } else if (holiday.equals("fourth of july")) {
            backgroundColor1 = Color.valueOf(Color.rgb(0, 55, 184));
            backgroundColor2 = Color.valueOf(Color.rgb(196, 23, 0));
            textColor1 = Color.valueOf(Color.rgb(255, 255, 255));
            textColor2 = Color.valueOf(Color.rgb(186, 221, 255));
        } else if (holiday.equals("thanksgiving")) {
            backgroundColor1 = Color.valueOf(Color.rgb(235, 196, 0));
            backgroundColor2 = Color.valueOf(Color.rgb(71, 58, 6));
            textColor1 = Color.valueOf(Color.rgb(255, 166, 0));
            textColor2 = Color.valueOf(Color.rgb(224, 158, 43));
        }
    }

    public static Optional<HolidayTheme> getHolidayTheme(Date date) {
        if (date.getMonth() == 11 && date.getDate() == 25) {
            return Optional.of(new HolidayTheme("christmas"));
        } else if (date.getMonth() == 2 && date.getDate() == 17) {
            return Optional.of(new HolidayTheme("st pattys"));
        } else if (date.getMonth() == 6 && date.getDate() == 4) {
            return Optional.of(new HolidayTheme("fourth of july"));
        } else if (date.getMonth() == 10 && date.getDate() == 28) {
            return Optional.of(new HolidayTheme("thanksgiving"));
        }
        return Optional.empty();
    }

    public Color getBackgroundColor1() {
        return backgroundColor1;
    }

    public Color getBackgroundColor2() {
        return backgroundColor2;
    }

    public Color getTextColor1() {
        return textColor1;
    }

    public Color getTextColor2() {
        return textColor2;
    }
}
