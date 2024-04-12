package com.example.spotifywrapped.spotify;

import androidx.annotation.NonNull;

public class Me {
    public String displayName;
    public String email;
    public String href;
    public String uri;

    @NonNull
    @Override
    public String toString() {
        return "SpotifyMe{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", href='" + href + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
