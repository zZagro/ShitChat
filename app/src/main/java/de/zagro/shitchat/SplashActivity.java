package de.zagro.shitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.client.ShitChatClient;
import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.shitchat.user.User;
import de.zagro.shitchat.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    Button goToRegistration, goToLogin, loginButton;
    View registerUnderline, loginUnderline;

    TextView goToRegisterText;
    int inactiveColor, activeColor;
    boolean onLogin = true;
    boolean onRegister = false;

    ActivitySplashBinding binding;

    public static Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //checkVersion();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.splash_container);
        NavController navController = navHostFragment.getNavController();

        client = new Client("denzo.algoholics.eu", 25565);

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

            Future<Optional<String>> optional = SplashActivity.client.login(email, stringToPass(password));

            try {
                if (optional.get().isPresent())
                {
                    String errorMessage = optional.get().get();
                    client.sendErrorMessages(errorMessage, this);
                }
                else
                {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("status", "Already logged in!");
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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

    private void checkVersion()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("App").document("general-information");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists())
                    {
                        double firestoreVersion = document.getDouble("version");
                        double installedVersion = getVersion();

                        if (installedVersion < firestoreVersion)
                        {
                            Toast.makeText(SplashActivity.this, "Update to the latest version!", Toast.LENGTH_SHORT).show();
                            finish();
                            System.exit(0);
                        }
                    }
                    else
                    {
                        Log.d("FIRESTORE", "No such document");
                        Toast.makeText(SplashActivity.this, "Internal error: Database Document missing!", Toast.LENGTH_SHORT).show();
                        finish();
                        System.exit(0);
                    }
                }
                else
                {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                    Toast.makeText(SplashActivity.this, "Internal error: Database error!", Toast.LENGTH_SHORT).show();
                    finish();
                    System.exit(0);
                }
            }
        });
    }

    public double getVersion()
    {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return Double.parseDouble(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public class Client extends ShitChatClient
    {

        public Client(String address, int port) {
            super(address, port);
        }

        @Override
        public void onChangePasswordFailed(String s) {

        }

        @Override
        public void onChangePassword() {

        }

        @Override
        public void onPPChangeFailed(String s) {

        }

        @Override
        public void onPPChange(User user) {

        }

        @Override
        public void onUserNameChange(User user) {

        }

        @Override
        public void onUserNameChangeFailed(String s) {

        }

        @Override
        public void onAuthenticationFailed(String s) {

        }

        @Override
        public void onAuthSuccess() {

        }

        @Override
        public void onSearchUser(List<User> list) {

        }

        @Override
        public void onSearchUserFailed(String s) {

        }

        @Override
        public void onRequestSuccessful(UUID uuid, RequestType requestType) {

        }

        @Override
        public void onRequestFailed(String s, UUID uuid, RequestType requestType) {

        }

        @Override
        public void onRequestReceived(User user, RequestType requestType) {
            Toast.makeText(SplashActivity.this, user.getUsername() + " sent " + requestType, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectFailed() {

        }

        @Override
        public void onUserUpdated() {

        }

        @Override
        public void onDisconnect() {

        }

        @Override
        public void onConnect() {

        }

        public void sendErrorMessages(String errorMessage, Context context)
        {
            if (errorMessage.equals(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS))
                Toast.makeText(context, "The Account already exists!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.ACCOUNT_NONEXISTENT))
                Toast.makeText(context, "The Account does not exist!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.INTERNAL_ERROR))
                Toast.makeText(context, "Something went wrong! Try again later!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.INVALID_SESSION))
                Toast.makeText(context, "Your Session is invalid!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.INVALID_USERNAME))
                Toast.makeText(context, "The Username is not correct!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.NOT_AUTHENTICATED))
                Toast.makeText(context, "You are not signed in!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.NOT_CONNECTED))
                Toast.makeText(context, "You are not connected! Restart the app.", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.USERNAME_ALREADY_EXISTS))
                Toast.makeText(context, "The username you are trying to use already exists!", Toast.LENGTH_SHORT).show();

            if (errorMessage.equals(ShitChatPlaceholder.WRONG_PASSWORD))
                Toast.makeText(context, "Wrong Password!", Toast.LENGTH_SHORT).show();
        }
    }
}