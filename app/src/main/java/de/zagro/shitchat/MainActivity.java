package de.zagro.shitchat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import de.zagro.shitchat.databinding.ActivityMainBinding;
import de.zagro.shitchat.ui.toolbar.ToolbarManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_direct, R.id.navigation_groups, R.id.navigation_settings).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String currentFragment = Objects.requireNonNull(navController.getCurrentDestination()).getLabel().toString();
                Log.i("Current Fragment", currentFragment);
                if (item.getItemId() == R.id.navigation_home)
                {
                    switch (currentFragment)
                    {
                        case Constants.directFragment:
                            navController.navigate(R.id.action_navigation_direct_to_navigation_home);
                            break;
                        case Constants.groupsFragment:
                            navController.navigate(R.id.action_navigation_groups_to_navigation_home);
                            break;
                        case Constants.settingsFragment:
                            navController.navigate(R.id.action_navigation_settings_to_navigation_home);
                            break;
                    }
                }
                else if (item.getItemId() == R.id.navigation_direct)
                {
                    switch (currentFragment)
                    {
                        case Constants.homeFragment:
                            navController.navigate(R.id.action_navigation_home_to_navigation_direct);
                            break;
                        case Constants.groupsFragment:
                            navController.navigate(R.id.action_navigation_groups_to_navigation_direct);
                            break;
                        case Constants.settingsFragment:
                            navController.navigate(R.id.action_navigation_settings_to_navigation_direct);
                            break;
                    }
                }
                else if (item.getItemId() == R.id.navigation_groups)
                {
                    switch (currentFragment)
                    {
                        case Constants.homeFragment:
                            navController.navigate(R.id.action_navigation_home_to_navigation_groups);
                            break;
                        case Constants.directFragment:
                            navController.navigate(R.id.action_navigation_direct_to_navigation_groups);
                            break;
                        case Constants.settingsFragment:
                            navController.navigate(R.id.action_navigation_settings_to_navigation_groups);
                            break;
                    }
                }
                else if (item.getItemId() == R.id.navigation_settings)
                {
                    switch (currentFragment)
                    {
                        case Constants.homeFragment:
                            navController.navigate(R.id.action_navigation_home_to_navigation_settings);
                            break;
                        case Constants.directFragment:
                            navController.navigate(R.id.action_navigation_direct_to_navigation_settings);
                            break;
                        case Constants.groupsFragment:
                            navController.navigate(R.id.action_navigation_groups_to_navigation_settings);
                            break;
                    }
                }
                return true;
            }
        });

        getArgs();
    }

    private void getArgs()
    {
        if (getIntent() == null) return;

        if (getIntent().getStringExtra("status") != null)
        {
            if (getIntent().getStringExtra("status").equals("Signup"))
            {
                navController.navigate(R.id.action_navigation_home_to_navigation_settings);
            }
        }

        if (getIntent().getStringExtra("request") != null)
        {
            if (getIntent().getStringExtra("request").equals("Direct"))
            {
                navController.navigate(R.id.action_navigation_home_to_navigation_direct);
            }
            else if (getIntent().getStringExtra("request").equals("Groups"))
            {
                navController.navigate(R.id.action_navigation_home_to_navigation_groups);
            }
        }
    }
}