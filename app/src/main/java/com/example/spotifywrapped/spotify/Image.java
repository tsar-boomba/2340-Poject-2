package com.example.spotifywrapped.spotify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Image {
    public String url;
    @Nullable
    public Integer height;
    @Nullable
    public Integer width;

    @NonNull
    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
