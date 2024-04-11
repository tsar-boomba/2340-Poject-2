package com.example.spotifywrapped;

import static com.example.spotifywrapped.Utils.unblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spotifywrapped.AppDatabase;
import com.example.spotifywrapped.databinding.UpdateUserInfoBinding;
import com.example.spotifywrapped.entities.User;
import com.example.spotifywrapped.entities.UserDao;

public class UpdateUserInfo extends Fragment {
    private UpdateUserInfoBinding binding;
    private AppDatabase db;
    private UserDao userDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = UpdateUserInfoBinding.inflate(inflater, container, false);

        db = ((MainActivity) requireActivity()).getDb();
        userDao = db.userDao();

        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        return binding.getRoot();
    }

    private void updateUser() {
        String firstName = binding.editTextFirstName.getText().toString().trim();
        String lastName = binding.editTextLastName.getText().toString().trim();
        String username = binding.editTextUsername.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find user by first name and last name
        User user = userDao.findByUsername(username);
        if (user != null) {
            // Update user information
            user.firstName = firstName;
            user.lastName = lastName;
            user.username = username;
            user.password = password;

            // Update user in database
            unblock(() -> {
                userDao.insertAll(user);
                Toast.makeText(requireActivity(), "User information updated successfully", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(requireActivity(), "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser() {
        String username = binding.editTextUsername.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill in first name and last name", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDao.findByUsername(username);
        if (user != null) {
            unblock(() -> {
                userDao.delete(user);
                Toast.makeText(requireActivity(), "User deleted successfully", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(requireActivity(), "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        db = null;
    }
}
