package com.example.spotifywrapped;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spotifywrapped.databinding.FragmentLoginBinding;
import com.example.spotifywrapped.spotify.Spotify;
import com.example.spotifywrapped.spotify.Timeframe;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private LoginViewModel viewModel;
    private FragmentLoginBinding binding;
    private Spotify spotify;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        MainActivity activity = (MainActivity) requireActivity();
        spotify = activity.getSpotify();

        spotify.setOnResult(() -> {
            Log.i(TAG, "onCreateView: Spotify done");
            spotify.getTopTracks(requireActivity(), Timeframe.ONE_YEAR, (res) -> {
                Log.i(TAG, "onCreateView: " + res.get());
            });
        });

        binding.loginButton.setOnClickListener((e) -> {
            activity.getToken();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        spotify = null;
    }
}