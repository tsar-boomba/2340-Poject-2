package com.example.spotifywrapped.entities;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spotifywrapped.databinding.WrappedBinding;
import com.example.spotifywrapped.spotify.Artist;
import com.example.spotifywrapped.spotify.SimplifiedArtist;
import com.example.spotifywrapped.spotify.Spotify;
import com.example.spotifywrapped.spotify.Timeframe;
import com.example.spotifywrapped.spotify.TopTracks;
import com.example.spotifywrapped.spotify.Track;
import com.example.spotifywrapped.theme.HolidayTheme;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Wrapped {
    private static final String TAG = "Wrapped";
    public Artist[] topArtists;
    public Track[] topTracks;
    public String[] topGenres;
    public String createdAt;
    public String name;
    public Timeframe timeframe;
    @Nullable
    public String themeName;

    public static class Adapter implements ListAdapter {
        private List<Wrapped> wrappeds;
        private HashSet<DataSetObserver> observers = new HashSet<>();

        public Adapter(List<Wrapped> wrappeds) {
            this.wrappeds = wrappeds;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            observers.add(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }

        @Override
        public int getCount() {
            return wrappeds.size();
        }

        @Override
        public Object getItem(int position) {
            return wrappeds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return wrappeds.get(position).name.hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WrappedBinding binding;
            if (convertView != null) {
                binding = WrappedBinding.bind(convertView);
            } else {
                binding = WrappedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            }

            binding.wrappedName.setText(wrappeds.get(position).name);
            // TODO: format this
            binding.wrappedCreatedAt.setText("Created At: " + wrappeds.get(position).createdAt);

            return binding.getRoot();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return wrappeds.isEmpty();
        }
    }

    @NonNull
    public static Wrapped fromTopTracks(Spotify spotify, TopTracks topTracks, Timeframe timeframe, @Nullable String themeName, int numTracks) {
        Log.i(TAG, "fromTopTracks: " + topTracks);
        Wrapped wrapped = new Wrapped();
        wrapped.timeframe = timeframe;
        wrapped.themeName = themeName;
        wrapped.createdAt = LocalDateTime.now().toString();

        HashMap<Artist, Integer> artistToAppearances = new HashMap<>();
        HashMap<String, Integer> genreToAppearances = new HashMap<>();

        for (Track track : topTracks.items) {
            for (Artist artist : track.artists) {
                // Turns from partial artist to full with API request
                artist.populate(spotify);

                setOrIncrement(artistToAppearances, artist);

                for (String genre : artist.genres) {
                    setOrIncrement(genreToAppearances, genre);
                }
            }
        }

        List<Map.Entry<Artist, Integer>> artistList = new ArrayList<>(artistToAppearances.entrySet());
        List<Map.Entry<String, Integer>> genreList = new ArrayList<>(genreToAppearances.entrySet());

        // Sort Descending
        artistList.sort((a, b) -> b.getValue() - a.getValue());
        genreList.sort((a, b) -> b.getValue() - a.getValue());

        Log.i(TAG, "fromTopTracks: " + artistList);
        Log.i(TAG, "fromTopTracks: " + genreList);

        wrapped.topTracks = Arrays.copyOf(topTracks.items, numTracks);
        wrapped.topArtists = Arrays.copyOf(artistList.stream().map(Map.Entry::getKey).toArray(Artist[]::new), 10);
        wrapped.topGenres =  Arrays.copyOf(genreList.stream().map(Map.Entry::getKey).toArray(String[]::new), 5);

        return wrapped;
    }

    private static <K> void setOrIncrement(Map<K, Integer> map, K key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Wrapped{" +
                "topArtists=" + Arrays.toString(topArtists) +
                ", topTracks=" + Arrays.toString(topTracks) +
                ", topGenres=" + Arrays.toString(topGenres) +
                ", createdAt='" + createdAt + '\'' +
                ", name='" + name + '\'' +
                ", timeframe=" + timeframe +
                '}';
    }
}
