package de.zagro.shitchat.ui.settings;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import de.zagro.shitchat.ChangeEmailActivity;
import de.zagro.shitchat.ChangePasswordActivity;
import de.zagro.shitchat.ChangeUsernameActivity;
import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.UserImageActivity;
import de.zagro.shitchat.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private ImageView userIcon, userIconCircle, usernameArrow, emailArrow;
    private TextView usernameLabel, usernameText, emailLabel, emailText;
    private MaterialButton editUserIconBtn, changePasswordBtn, logoutBtn;

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSharedElementEnterTransition(new ChangeBounds().setDuration(200));
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userIcon = binding.userIcon;
        editUserIconBtn = binding.userIconEditButton;
        userIconCircle = binding.userIconCircle;

        usernameArrow = binding.usernameArrow;
        usernameLabel = binding.usernameLabel;
        usernameText = binding.usernameText;

        emailArrow = binding.emailArrow;
        emailLabel = binding.emailLabel;
        emailText = binding.emailText;

        changePasswordBtn = binding.changePasswordButton;

        logoutBtn = binding.logoutButton;

        setUserData();
        editUserIcon();
        changeUsername();
        changeEmail();
        changePassword();
        logout();
    }

    private void setUserData()
    {
        userIcon.setImageDrawable(Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name"));
        usernameText.setText(SplashActivity.client.getUser().getUsername());
        emailText.setText(SplashActivity.client.getEmail());
    }

    private void logout()
    {
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Logout", "Logout btn clicked!");
                SharedPreferences userDetails = requireActivity().getApplicationContext().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = userDetails.edit();
                edit.clear();
                edit.apply();

                Intent intent = new Intent(requireActivity(), SplashActivity.class);
                startActivity(intent);
                requireActivity().finish();

                SplashActivity.client.disconnect();
            }
        });
    }

    private void changeUsername()
    {
        View.OnClickListener usernameClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ChangeUsernameActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        };

//        usernameArrow.setOnClickListener(usernameClick);
//        usernameText.setOnClickListener(usernameClick);
//        usernameLabel.setOnClickListener(usernameClick);
        binding.settingsUsernameBg.setOnClickListener(usernameClick);
    }

    private void changeEmail()
    {
        View.OnClickListener emailClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireActivity(), "Not yet implemented! I suggest creating a new account.", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(requireActivity(), ChangeEmailActivity.class);
//                startActivity(intent);
            }
        };

//        emailArrow.setOnClickListener(emailClick);
//        emailLabel.setOnClickListener(emailClick);
//        emailText.setOnClickListener(emailClick);
        binding.settingsEmailBg.setOnClickListener(emailClick);
    }

    private void changePassword()
    {
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    private void editUserIcon()
    {
        View.OnClickListener userIconClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UserImageActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), Pair.create(userIcon, "user_image"), Pair.create(userIconCircle, "user_image_frame"));
                startActivity(intent, options.toBundle());
            }
        };

        userIcon.setOnClickListener(userIconClick);
        editUserIconBtn.setOnClickListener(userIconClick);
    }

}
