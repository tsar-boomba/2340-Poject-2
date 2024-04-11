package com.example.spotifywrapped.spotify;

import androidx.annotation.NonNull;

public class SimplifiedArtist {
    public String id;
    public String name;
    public String href;
    public String uri;
    public ExternalUrls externalUrls;

    @NonNull
    @Override
    public String toString() {
        return "SimplifiedArtist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", href='" + href + '\'' +
                ", uri='" + uri + '\'' +
                ", externalUrls=" + externalUrls +
                '}';
    }
}
