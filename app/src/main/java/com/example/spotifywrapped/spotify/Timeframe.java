package com.example.spotifywrapped.spotify;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum Timeframe implements Serializable {
    ONE_MONTH,
    SIX_MONTHS,
    ONE_YEAR;

    @NonNull
    public String toApiParam() {
        switch (this) {
            case ONE_MONTH:
                return "short_term";
            case SIX_MONTHS:
                return "medium_term";
            case ONE_YEAR:
                return "long_term";
        }

        throw new RuntimeException("Uhm what the sigma?");
    }
}
