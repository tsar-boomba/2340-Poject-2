package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.spotifywrapped.databinding.FragmentSettingsBinding;
import com.example.spotifywrapped.entities.User;
import com.mohamedabulgasem.loadingoverlay.LoadingAnimation;
import com.mohamedabulgasem.loadingoverlay.LoadingOverlay;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private FragmentSettingsBinding binding;
    private AppDatabase db;
    private User user;
    private LoadingOverlay loadingOverlay;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        MainActivity activity = (MainActivity) requireActivity();
        loadingOverlay = LoadingOverlay.Companion.with(requireActivity(), LoadingAnimation.BuiltinAnimations.getLOADING_SPINNER(), 0.5f, false, null, null, null);
        db = activity.getDb();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        unblock(() -> {
            viewModel.setUser(db.userDao().getAll().get(0));
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), (user) -> {
            if (user == null) {
                loadingOverlay.show();
            } else {
                // Set up UI
                loadingOverlay.dismiss();
                binding.editFirstName.setText(user.firstName);
                binding.editLastName.setText(user.lastName);
                binding.editUsername.setText(user.username);
                binding.editPassword.setText(user.password);
                binding.updateUser.setOnClickListener((e) -> {
                    String firstName = binding.editFirstName.getText().toString().trim();
                    String lastName = binding.editLastName.getText().toString().trim();
                    String username = binding.editUsername.getText().toString().trim();
                    String password = binding.editPassword.getText().toString().trim();

                    if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                        showError("No fields can be empty");
                    }

                    loadingOverlay.show();
                    unblock(() -> {
                        try {
                            User currUser = db.userDao().getAll().get(0);
                            currUser.firstName = firstName;
                            currUser.lastName = lastName;
                            currUser.username = username;
                            currUser.password = password;
                            db.userDao().update(currUser);

                            requireActivity().runOnUiThread(() -> {
                                showError("Updated User info! Please log back in! :D");
                                NavDirections nav = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment();
                                Navigation.findNavController(getView()).navigate(nav);
                            });
                        } finally {
                            loadingOverlay.dismiss();
                        }
                    });
                });

                binding.deleteUser.setOnClickListener((e) -> {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Delete Account")
                            .setMessage("Are you sure you want to delete your account?")
                            .setPositiveButton("Delete", (d, i) -> {
                                unblock(() -> {
                                    db.clearAllTables();
                                    requireActivity().runOnUiThread(() -> {
                                        showError("Deleted User info! :D");
                                        NavDirections nav = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment();
                                        Navigation.findNavController(getView()).navigate(nav);
                                    });
                                });
                            }).setNegativeButton("Cancel", (d, i) -> {
                                d.dismiss();
                            }).show();
                });
            }
        });
    }

    private void showError(String message) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}