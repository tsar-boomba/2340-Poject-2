package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.spotifywrapped.databinding.FragmentWrappedBinding;
import com.example.spotifywrapped.databinding.TrackBinding;
import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.entities.Wrapped;
import com.example.spotifywrapped.spotify.Artist;
import com.example.spotifywrapped.spotify.Spotify;
import com.example.spotifywrapped.spotify.Timeframe;
import com.example.spotifywrapped.spotify.TopTracks;
import com.example.spotifywrapped.spotify.Track;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.mohamedabulgasem.loadingoverlay.LoadingAnimation;
import com.mohamedabulgasem.loadingoverlay.LoadingOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WrappedFragment extends Fragment {
    private static final String TAG = "WrappedFragment";
    private static final String GEMINI = "AIzaSyCHZHjXLwmeYgmwdQ1sFKqwTsnemEpTFXg";
    private WrappedViewModel viewModel;
    private FragmentWrappedBinding binding;
    private LoadingOverlay loadingOverlay;
    private AppDatabase db;
    private Spotify spotify;
    private boolean saved = false;

    public static WrappedFragment newInstance() {
        return new WrappedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWrappedBinding.inflate(inflater, container, false);
        spotify = ((MainActivity) requireActivity()).getSpotify();
        db = ((MainActivity) requireActivity()).getDb();
        loadingOverlay = LoadingOverlay.Companion.with(requireActivity(), LoadingAnimation.BuiltinAnimations.getLOADING_SPINNER(), 0.5f, false, null, null, null);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WrappedViewModel.class);
        WrappedFragmentArgs args = WrappedFragmentArgs.fromBundle(getArguments());

        final TopTracks topTracks = new Gson().fromJson(args.getTopTracks(), TopTracks.class);
        final Timeframe timeframe = args.getTimeframe();
        Optional<Wrapped> preMadeWrapped = Optional
                .ofNullable(args.getWrapped())
                .map((wrappedStr) -> new Gson().fromJson(wrappedStr, Wrapped.class));

        saved = preMadeWrapped.isPresent();

        viewModel.setWrapped(preMadeWrapped);

        viewModel.getMessage().observe(getViewLifecycleOwner(), (llmMessage) -> {
            loadingOverlay.dismiss();
            if (llmMessage == null) {
                return;
            }

            String prompt = llmMessage.first;
            String result = llmMessage.second;

            new AlertDialog.Builder(requireActivity())
                    .setTitle("The LLM Says")
                    .setMessage("Prompt: " + prompt + "\n\n" + result)
                    .setPositiveButton("Awesome!", (d, i) -> {
                        d.dismiss();
                    })
                    .setNegativeButton("Boooring!", (d, i) -> {
                        d.dismiss();
                    })
                    .show();
        });

        loadingOverlay.show();
        viewModel.getWrapped().observe(getViewLifecycleOwner(), (wrappedOpt) -> {
            if (wrappedOpt.isPresent()) {
                // Setup UI for wrapped
                Wrapped wrapped = wrappedOpt.get();
                Log.i(TAG, wrapped.toString());

                for (int i = 0; i < wrapped.topTracks.length; i++) {
                    Track track = wrapped.topTracks[i];
                    TrackBinding trackBinding = TrackBinding.inflate(getLayoutInflater(), binding.wrappedTopTracks, false);

                    trackBinding.trackRank.setText(String.valueOf(i + 1) + ".");
                    trackBinding.trackTitle.setText(track.name);
                    trackBinding.trackArtists.setText(String.join(", ", Arrays.stream(track.artists).map((a) -> a.name).toArray(String[]::new)));

                    if (track.album.images != null && track.album.images.length > 0) {
                        unblock(() -> {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(
                                        new OkHttpClient().newCall(
                                                new Request.Builder().get().url(track.album.images[0].url).build()
                                        ).execute().body().byteStream());
                                trackBinding.trackAlbumImage.post(() -> {
                                    trackBinding.trackAlbumImage.setImageBitmap(bitmap);
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "onViewCreated: Failed to get image", e);
                            }
                        });
                    }

                    binding.wrappedTopTracks.addView(trackBinding.getRoot());
                }

                for (int i = 0; i < wrapped.topArtists.length; i++) {
                    Artist artist = wrapped.topArtists[i];
                    TextView topArtist = new TextView(requireContext());
                    topArtist.setText(String.valueOf(i + 1) + ". " + artist.name);
                    topArtist.setTextSize(28f);
                    binding.wrappedTopArtists.addView(topArtist);
                }

                for (int i = 0; i < wrapped.topGenres.length; i++) {
                    String genre = wrapped.topGenres[i];
                    TextView topGenre = new TextView(requireContext());
                    topGenre.setText(String.valueOf(i + 1) + ". " + genre);
                    topGenre.setTextSize(28f);
                    binding.wrappedTopGenres.addView(topGenre);
                }

                if (!saved) {
                    binding.saveWrappedAsLabel.setVisibility(View.VISIBLE);
                    binding.saveWrappedAs.setVisibility(View.VISIBLE);
                    binding.saveWrappedButton.setVisibility(View.VISIBLE);
                }
                loadingOverlay.dismiss();

                binding.saveWrappedButton.setOnClickListener((e) -> {
                    String name = binding.saveWrappedAs.getText().toString().trim();

                    if (name.isEmpty()) {
                        showError("Cannot save with no name.");
                        return;
                    }

                    loadingOverlay.show();
                    unblock(() -> {
                        try {// Must be one user at this point
                            User user = db.userDao().getAll().get(0);
                            Gson gson = new Gson();

                            boolean exists = false;
                            if (user.wrappeds != null) {
                                List<Wrapped> wrappeds = user.wrappeds.stream().map((wrappedStr) -> gson.fromJson(wrappedStr, Wrapped.class)).collect(Collectors.toList());
                                exists = wrappeds.stream().anyMatch((wrapped1) -> wrapped1.name.equals(name));
                            }

                            if (exists) {
                                showError("There is already a wrapped with name `" + name + "`.");
                                return;
                            }

                            wrapped.name = name;
                            if (user.wrappeds != null) {
                                user.wrappeds.add(gson.toJson(wrapped));
                            } else {
                                List<String> wrappeds = new ArrayList<>(1);
                                wrappeds.add(gson.toJson(wrapped));
                                user.wrappeds = wrappeds;
                            }

                            db.userDao().update(user);

                            requireActivity().runOnUiThread(() -> {
                                NavDirections nav = WrappedFragmentDirections.actionWrappedFragmentToMainFragment(user, null);
                                Navigation.findNavController(getView()).navigate(nav);
                            });
                        } finally {
                            loadingOverlay.dismiss();
                        }
                    });
                });

                binding.askLlmButton.setOnClickListener((e) -> {
                    loadingOverlay.show();

                    unblock(() -> {
                        try {
                            GenerativeModel gm = new GenerativeModel("gemini-1.0-pro", GEMINI);
                            GenerativeModelFutures generativeModelFutures = GenerativeModelFutures.from(gm);
                            Executor executor = Executors.newSingleThreadExecutor();
                            String prompt = getPrompt(wrapped);
                            Content content = new Content.Builder().addText(prompt).build();
                            ListenableFuture<GenerateContentResponse> fut = generativeModelFutures.generateContent(content);
                            fut.addListener(() -> {
                                try {
                                    viewModel.setMessage(new Pair<>(prompt, fut.get().getText()));
                                } catch (Exception ex) {
                                    Log.e(TAG, "Problem getting LLM res: ", ex);
                                    viewModel.setMessage(null);
                                }
                            }, executor);
                        } catch (Exception ex) {
                            Log.e(TAG, "Problem starting LLM: ", ex);
                            viewModel.setMessage(null);
                        }
                    });
                });
            } else {
                // Asynchronously create wrapped from top tracks
                loadingOverlay.show();

                unblock(() -> {
                    viewModel.setWrapped(Optional.of(Wrapped.fromTopTracks(spotify, topTracks, timeframe, 10)));
                });
            }
        });
    }

    private String getPrompt(Wrapped wrapped) {
        Random rand = new Random();
        int category = rand.nextInt(3);

        if (category == 0) {
            // Tracks
            Track track = wrapped.topTracks[rand.nextInt(wrapped.topTracks.length)];
            return "What's some food to eat while listening to " + track.name + " by " + String.join(", ", Arrays.stream(track.artists).map((a) -> a.name).toArray(String[]::new));
        } else if (category == 1) {
            // Artists
            Artist artist = wrapped.topArtists[rand.nextInt(wrapped.topArtists.length)];
            return "What's a fan of " + artist.name + "'s music's favorite color?";
        } else {
            // Genres
            String genre = wrapped.topGenres[rand.nextInt(wrapped.topGenres.length)];
            return "What's outfit would a fan of the " + genre + " genre wear?";
        }
    }

    ;

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        viewModel = null;
    }
}