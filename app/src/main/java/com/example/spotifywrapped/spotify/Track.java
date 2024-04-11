package com.example.spotifywrapped.spotify;

public class Track {
    public Album album;
    public String name;
    public String href;
    public int popularity;

    @Override
    public String toString() {
        return "Track{" +
                "album=" + album +
                ", name='" + name + '\'' +
                ", href='" + href + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
