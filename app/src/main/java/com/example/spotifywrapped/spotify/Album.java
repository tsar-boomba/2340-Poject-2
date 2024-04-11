package com.example.spotifywrapped.spotify;

import java.util.Arrays;

public class Album {
    public SimplifiedArtist[] artists;
    public Image[] images;
    public String name;
    public int totalTracks;
    public String href;

    @Override
    public String toString() {
        return "Album{" +
                "artists=" + Arrays.toString(artists) +
                ", images=" + Arrays.toString(images) +
                ", name='" + name + '\'' +
                ", totalTracks=" + totalTracks +
                ", href='" + href + '\'' +
                '}';
    }
}
