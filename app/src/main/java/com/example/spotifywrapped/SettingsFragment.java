package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.dialogTitle;
import static com.example.spotifywrapped.Utils.unblock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.dolatkia.animatedThemeManager.AppTheme;
import com.dolatkia.animatedThemeManager.ThemeFragment;
import com.example.spotifywrapped.databinding.FragmentSettingsBinding;
import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.theme.HolidayTheme;
import com.example.spotifywrapped.theme.LightTheme;
import com.example.spotifywrapped.theme.MyAppTheme;
import com.mohamedabulgasem.loadingoverlay.LoadingAnimation;
import com.mohamedabulgasem.loadingoverlay.LoadingOverlay;

public class SettingsFragment extends ThemeFragment {

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
                MainActivity activity = (MainActivity) requireActivity();
                SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

                loadingOverlay.dismiss();
                binding.editFirstName.setText(user.firstName);
                binding.editLastName.setText(user.lastName);
                binding.editUsername.setText(user.username);
                binding.editPassword.setText(user.password);

                binding.notificationSwitch.setChecked(sharedPreferences.getBoolean("notifications", false));

                binding.notificationSwitch.setOnCheckedChangeListener((switchView, checked) -> {
                    sharedPreferences.edit().putBoolean("notifications", checked).apply();
                    binding.notificationSwitch.setChecked(sharedPreferences.getBoolean("notifications", false));
                });

                binding.toggleTheme.setOnClickListener((button) -> {
                    MyAppTheme currentTheme = activity.getCurrentTheme();

                    if (currentTheme.getClass() == HolidayTheme.class) {
                        // Don't change cause its a holiday (wooo)
                        return;
                    }

                    if (currentTheme.getClass() == LightTheme.class) {
                        activity.setTheme(false, button);
                    } else {
                        activity.setTheme(true, button);
                    }
                });

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
                    new AlertDialog.Builder(activity)
                            .setCustomTitle(dialogTitle(activity, activity.getCurrentTheme(), "Delete Account"))
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

    @Override
    public void syncTheme(@NonNull AppTheme appTheme) {
        MyAppTheme theme = (MyAppTheme) appTheme;
        Context context = getContext();

        if (context == null) {
            return;
        }

        int textColor = theme.textColor(context);

        binding.editFirstNameLabel.setTextColor(textColor);
        binding.editFirstName.setTextColor(textColor);

        binding.editLastNameLabel.setTextColor(textColor);
        binding.editLastName.setTextColor(textColor);

        binding.editUsernameLabel.setTextColor(textColor);
        binding.editUsername.setTextColor(textColor);

        binding.editPasswordLabel.setTextColor(textColor);
        binding.editPassword.setTextColor(textColor);

        binding.notificationSwitchLabel.setTextColor(textColor);
        binding.notificationSwitch.setTextColor(theme.buttonColor(context));

        binding.toggleTheme.setTextColor(textColor);
        binding.toggleTheme.setBackgroundColor(theme.buttonColor(context));

        binding.updateUser.setTextColor(textColor);
        binding.updateUser.setBackgroundColor(theme.buttonColor(context));

        binding.deleteUser.setTextColor(textColor);
        binding.deleteUser.setBackgroundColor(theme.buttonColor(context));
    }
}