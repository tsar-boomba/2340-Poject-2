package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.spotifywrapped.databinding.FragmentMainBinding;
import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.entities.Wrapped;
import com.example.spotifywrapped.spotify.Spotify;
import com.example.spotifywrapped.spotify.Timeframe;
import com.example.spotifywrapped.spotify.TopTracks;
import com.google.gson.Gson;
import com.mohamedabulgasem.loadingoverlay.LoadingAnimation;
import com.mohamedabulgasem.loadingoverlay.LoadingOverlay;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private MainViewModel viewModel;
    private FragmentMainBinding binding;
    private LoadingOverlay loadingOverlay;
    private Spotify spotify;
    private AppDatabase db;
    private String token;
    private User user;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        loadingOverlay = LoadingOverlay.Companion.with(requireActivity(), LoadingAnimation.BuiltinAnimations.getLOADING_SPINNER(), 0.5f, false, null, null, null);
        MainActivity activity = (MainActivity) requireActivity();
        spotify = activity.getSpotify();
        db = activity.getDb();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        unblock(() -> {
            // Load in top tracks for one month
            if (spotify == null) {
                if (token != null) {
                    spotify = ((MainActivity) requireActivity()).createSpotifyFromToken(token);
                } else {
                    throw new RuntimeException("Lost Spotify client!");
                }
            };
            spotify.getTopTracks(requireActivity(), Timeframe.ONE_MONTH, (oneMonth) -> {
                viewModel.setOneMonthTracks(oneMonth.get());
                spotify.getTopTracks(requireActivity(), Timeframe.SIX_MONTHS, (sixMonths) -> {
                    viewModel.setSixMonthTracks(sixMonths.get());
                    spotify.getTopTracks(requireActivity(), Timeframe.ONE_YEAR, (oneYear) -> {
                        viewModel.setOneYearTracks(oneYear.get());
                    });
                });
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainFragmentArgs args = MainFragmentArgs.fromBundle(getArguments());
        user = args.getUser();
        token = args.getToken();

        final AtomicReference<Timeframe> timeframe = new AtomicReference<>(Timeframe.ONE_MONTH);

        binding.createWrappedButton.setOnClickListener((e) -> {
            Log.i(TAG, "Create wrapped clicked!");
            AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                    .setTitle("Create a Wrapped - Pick a time frame")
                    .setSingleChoiceItems(new String[] {"One Month", "Six Months", "One Year"}, 0, (d, i) -> {
                        if (i == 0) {
                            timeframe.set(Timeframe.ONE_MONTH);
                        } else if (i == 1) {
                            timeframe.set(Timeframe.SIX_MONTHS);
                        } else {
                            timeframe.set(Timeframe.ONE_YEAR);
                        }
                    })
                    .setPositiveButton("Create", (d, i) -> {
                        Log.i(TAG, "Creating with timeframe: " + timeframe.get().name());

                        TopTracks topTracks = null;
                        Timeframe timeframe1 = timeframe.get();

                        switch (timeframe1) {
                            case ONE_MONTH:
                                topTracks = Objects.requireNonNull(viewModel.getTopTracks().getValue()).getOneMonthTracks();
                                break;
                            case SIX_MONTHS:
                                topTracks = Objects.requireNonNull(viewModel.getTopTracks().getValue()).getSixMonthTracks();
                                break;
                            case ONE_YEAR:
                                topTracks = Objects.requireNonNull(viewModel.getTopTracks().getValue()).getOneYearTracks();
                                break;
                            default:
                                throw new RuntimeException("Timeframe wrong??");
                        }

                        NavDirections nav = MainFragmentDirections.actionMainFragmentToWrappedFragment(new Gson().toJson(topTracks), null, timeframe1);
                        d.dismiss();
                        Navigation.findNavController(getView()).navigate(nav);
                    })
                    .setNegativeButton("Cancel", (d, i) -> {
                        d.dismiss();
                    })
                    .show();
        });

        binding.viewWrappedButton.setOnClickListener((e) -> {
            Log.i(TAG, "View wrapped clicked!");
            final List<Wrapped> wrappeds = user.deserializeWrappeds();

            if (wrappeds.size() < 1) {
                Toast.makeText(requireActivity(), "Create a wrapped first!", Toast.LENGTH_SHORT).show();
                return;
            }

            AtomicReference<Wrapped> selected = new AtomicReference<>(wrappeds.get(0));

            new AlertDialog.Builder(requireActivity())
                    .setTitle("View A Saved Wrapped - Select One")
                    .setAdapter(new Wrapped.Adapter(wrappeds), (d, i) -> {
                        Wrapped wrapped = wrappeds.get(i);
                        Log.i(TAG, "onViewCreated: " + selected.get());

                        Gson gson = new Gson();
                        NavDirections nav = MainFragmentDirections.actionMainFragmentToWrappedFragment(
                                gson.toJson(viewModel.getTopTracks().getValue().getOneMonthTracks()),
                                gson.toJson(wrapped),
                                wrapped.timeframe
                        );
                        Navigation.findNavController(getView()).navigate(nav);

                        d.dismiss();
                    })
                    .setNegativeButton("Cancel", (d, i) -> {
                        d.dismiss();
                    })
                    .show();
        });

        binding.settingsButton.setOnClickListener((e) -> {
            Log.i(TAG, "Settings clicked!");
            NavDirections nav = MainFragmentDirections.actionMainFragmentToSettingsFragment();
            Navigation.findNavController(getView()).navigate(nav);
        });

        viewModel.getTopTracks().observe(getViewLifecycleOwner(), (topTracks) -> {
            if (topTracks.getOneMonthTracks() == null
                    || topTracks.getSixMonthTracks() == null
                    || topTracks.getOneYearTracks() == null) {
                // Still loading in all the tracks
                loadingOverlay.show();
            } else {
                // All tracks loaded in
                loadingOverlay.dismiss();
            }
        });
    }
}