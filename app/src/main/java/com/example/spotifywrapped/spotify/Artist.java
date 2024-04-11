package com.example.spotifywrapped.spotify;

import java.util.Arrays;

public class Artist {
    public ExternalUrls externalUrls;
    public String[] genres;
    public String href;
    public Image[] images;
    public String name;
    public int popularity;

    @Override
    public String toString() {
        return "Artist{" +
                "externalUrls=" + externalUrls +
                ", genres=" + Arrays.toString(genres) +
                ", href='" + href + '\'' +
                ", images=" + Arrays.toString(images) +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
