package com.example.spotifywrapped.spotify;

import java.util.Arrays;

public class Track {
    public Album album;
    public Artist[] artists;
    public String name;
    public String href;
    public int popularity;

    @Override
    public String toString() {
        return "Track{" +
                "album=" + album +
                ", artists=" + Arrays.toString(artists) +
                ", name='" + name + '\'' +
                ", href='" + href + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
