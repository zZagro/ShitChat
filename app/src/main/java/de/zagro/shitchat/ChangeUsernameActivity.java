package de.zagro.shitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import de.zagro.shitchat.databinding.ActivityChangeUsernameBinding;

public class ChangeUsernameActivity extends AppCompatActivity {

    ActivityChangeUsernameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivityChangeUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}