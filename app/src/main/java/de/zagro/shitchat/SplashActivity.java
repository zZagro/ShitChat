package de.zagro.shitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;

import de.ancash.shitchat.client.ShitChatClient;
import de.zagro.shitchat.databinding.ActivitySplashBinding;
import de.zagro.shitchat.ui.login.LoginFragment;

public class SplashActivity extends AppCompatActivity {

    Button goToRegistration, goToLogin, loginButton;
    View registerUnderline, loginUnderline;

    TextView goToRegisterText;
    int inactiveColor, activeColor;
    boolean onLogin = true;
    boolean onRegister = false;

    ActivitySplashBinding binding;
    public static ShitChatClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.splash_container);
        NavController navController = navHostFragment.getNavController();

        client = new ShitChatClient("denzo.algoholics.eu", 25565);
        AtomicReference<Boolean> connected = new AtomicReference<Boolean>(false);
        Thread t = new Thread(() -> connected.set(client.connect()));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (connected.get())
        {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

        if (isLoggedIn())
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("status", "Already logged in!");
            startActivity(intent);
            finish();
        }
    }

    public boolean isLoggedIn()
    {
        return false;
    }

    private void onClick(NavController navController)
    {
        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onLogin)
                {
                    goToRegistration.setTextColor(activeColor);
                    registerUnderline.setBackgroundColor(activeColor);

                    goToLogin.setTextColor(inactiveColor);
                    loginUnderline.setBackgroundColor(inactiveColor);

                    navController.navigate(R.id.action_loginFragment_to_registrationFragment);
                    onLogin = false;
                    onRegister = true;
                }
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRegister)
                {
                    goToRegistration.setTextColor(inactiveColor);
                    registerUnderline.setBackgroundColor(inactiveColor);

                    goToLogin.setTextColor(activeColor);
                    loginUnderline.setBackgroundColor(activeColor);

                    navController.navigateUp();
                    onLogin = true;
                    onRegister = false;
                }
            }
        });
    }
}