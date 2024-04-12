package com.example.spotifywrapped.spotify;

import android.util.Log;

import java.util.Arrays;
import java.util.Optional;

public class Artist {
    public ExternalUrls externalUrls;
    public String[] genres;
    public String href;
    public Image[] images;
    public String name;
    public int popularity;

    public void populate(Spotify spotify) {
        Optional<Artist> artistOpt = spotify.getArtistBlocking(href);

        if (artistOpt.isPresent()) {
            Artist artist = artistOpt.get();
            externalUrls = artist.externalUrls;
            genres = artist.genres;
            href = artist.href;
            images = artist.images;
            name = artist.name;
            popularity = artist.popularity;
        } else {
            Log.e("Artist", "populate: failed");
        }
    }

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
