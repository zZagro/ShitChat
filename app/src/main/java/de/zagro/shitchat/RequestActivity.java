package de.zagro.shitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.zagro.shitchat.databinding.ActivityRequestBinding;

public class RequestActivity extends AppCompatActivity {

    private ActivityRequestBinding binding;

    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.fragment_requests_search, R.id.fragment_requests_incoming, R.id.fragment_requests_outgoing).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_request);
        NavigationUI.setupWithNavController(binding.navRequests, navController);
    }
}