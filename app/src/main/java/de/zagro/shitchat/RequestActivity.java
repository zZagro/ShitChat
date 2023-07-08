package de.zagro.shitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

        navView = binding.navRequests;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.fragment_requests_search, R.id.fragment_requests_incoming, R.id.fragment_requests_outgoing).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_request);
        NavigationUI.setupWithNavController(binding.navRequests, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String currentFragment = navController.getCurrentDestination().getLabel().toString();
                Log.i("Current Fragment", currentFragment);
                if (item.getItemId() == R.id.nav_requests_search)
                {
                    switch (currentFragment)
                    {
                        case Constants.requestsIncoming:
                            navController.navigate(R.id.action_requestsIncoming_to_requestsSearch);
                            break;
                        case Constants.requestsOutgoing:
                            navController.navigate(R.id.action_requestsOutgoing_to_requestsSearch);
                            break;
                    }
                }
                else if (item.getItemId() == R.id.nav_requests_incoming)
                {
                    switch (currentFragment)
                    {
                        case Constants.requestsSearch:
                            navController.navigate(R.id.action_requestsSearch_to_requestsIncoming);
                            break;
                        case Constants.requestsOutgoing:
                            navController.navigate(R.id.action_requestsOutgoing_to_requestsIncoming);
                            break;
                    }
                }
                else if (item.getItemId() == R.id.nav_requests_outgoing)
                {
                    switch (currentFragment)
                    {
                        case Constants.requestsSearch:
                            navController.navigate(R.id.action_requestsSearch_to_requestsOutgoing);
                            break;
                        case Constants.requestsIncoming:
                            navController.navigate(R.id.action_requestsIncoming_to_requestsOutgoing);
                            break;
                    }
                }
                return true;
            }
        });
    }
}