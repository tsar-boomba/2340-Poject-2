package com.example.spotifywrapped.spotify;

import java.util.Arrays;

public class TopTracks {
    public Track[] items;

    @Override
    public String toString() {
        return "TopTracks{" +
                "items=" + Arrays.toString(items) +
                '}';
    }
}
