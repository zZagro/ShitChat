package de.zagro.shitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.client.ShitChatClient;
import de.ancash.shitchat.util.AuthenticationUtil;
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
    public static final ShitChatClient client = new ShitChatClient("denzo.algoholics.eu", 25565);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.splash_container);
        NavController navController = navHostFragment.getNavController();

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
        logIn();
    }

    public boolean isLoggedIn()
    {
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        return !userDetails.getString("email", "0").equals("0");
    }

    private void logIn()
    {
        if (isLoggedIn())
        {
            while(!client.isConnected()) Thread.yield();

            SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
            String email = userDetails.getString("email", null);
            String password = userDetails.getString("hashedPassword", null);

            Optional<String> optional = SplashActivity.client.login(email, stringToPass(password));

            if (optional.isPresent())
            {
                String errorMessage = optional.get();
                if (errorMessage.equals(ShitChatPlaceholder.WRONG_PASSWORD))
                    Toast.makeText(this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                if (errorMessage.equals(ShitChatPlaceholder.ACCOUNT_NONEXISTENT))
                    Toast.makeText(this, "The Account does not exist!", Toast.LENGTH_SHORT).show();
                if (errorMessage.equals(ShitChatPlaceholder.INTERNAL_ERROR))
                    Toast.makeText(this, "Something went wrong! Try again later!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("status", "Already logged in!");
                startActivity(intent);
                finish();
            }
        }
    }

    private static byte[] stringToPass(String s) {
        byte[] b2 = new byte[s.split(" ").length];
        int i = 0;
        for(byte by : Stream.of(s.split(" ")).map(Byte::valueOf).collect(Collectors.toList()))
            b2[i++] = by;
        return b2;
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