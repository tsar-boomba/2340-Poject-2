package com.example.spotifywrapped.spotify;

import androidx.annotation.NonNull;

public class AllTopTracks {
    private TopTracks oneMonthTracks;
    private TopTracks sixMonthTracks;
    private TopTracks oneYearTracks;

    public AllTopTracks(TopTracks oneMonthTracks, TopTracks sixMonthTracks, TopTracks oneYearTracks) {
        this.oneMonthTracks = oneMonthTracks;
        this.sixMonthTracks = sixMonthTracks;
        this.oneYearTracks = oneYearTracks;
    }

    public TopTracks getOneMonthTracks() {
        return oneMonthTracks;
    }

    public void setOneMonthTracks(TopTracks oneMonthTracks) {
        this.oneMonthTracks = oneMonthTracks;
    }

    public TopTracks getSixMonthTracks() {
        return sixMonthTracks;
    }

    public void setSixMonthTracks(TopTracks sixMonthTracks) {
        this.sixMonthTracks = sixMonthTracks;
    }

    public TopTracks getOneYearTracks() {
        return oneYearTracks;
    }

    public void setOneYearTracks(TopTracks oneYearTracks) {
        this.oneYearTracks = oneYearTracks;
    }

    @NonNull
    @Override
    public String toString() {
        return "AllTopTracks{" +
                "oneMonthTracks=" + oneMonthTracks +
                ", sixMonthTracks=" + sixMonthTracks +
                ", oneYearTracks=" + oneYearTracks +
                '}';
    }
}
