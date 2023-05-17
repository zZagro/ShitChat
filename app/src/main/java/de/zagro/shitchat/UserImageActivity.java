package de.zagro.shitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import de.zagro.shitchat.databinding.ActivityUserImageBinding;

public class UserImageActivity extends AppCompatActivity {

    private ActivityUserImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivityUserImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}