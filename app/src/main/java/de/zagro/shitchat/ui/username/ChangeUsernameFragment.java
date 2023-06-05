package de.zagro.shitchat.ui.username;

import android.content.Context;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentChangeUsernameBinding;

public class ChangeUsernameFragment extends Fragment {

    private ImageButton backButton;
    private TextView currentUsernameText, currentUsernameLabel, newUsernameLabel;
    private EditText newUsernameText;
    private MaterialButton confirmButton;

    private FragmentChangeUsernameBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeUsernameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUsernameText = binding.usernameCurrentText;
        currentUsernameLabel = binding.usernameCurrentLabel;
        newUsernameLabel = binding.usernameNewLabel;
        newUsernameText = binding.usernameNewText;
        confirmButton = binding.usernameBtnConfirm;
        backButton = binding.usernameBackButton;

        setCurrentUsername();
        playAnimations();
        onClick();
    }

    private void onClick()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUsernameText.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(newUsernameText.getWindowToken(), 0);
            }
        });
    }

    private void setCurrentUsername()
    {
        currentUsernameText.setText(SplashActivity.client.getUser().getName());
    }

    private void playAnimations()
    {
        Animation slideFromLeft = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_left);
        slideFromLeft.setDuration(300);
        currentUsernameLabel.startAnimation(slideFromLeft);
        currentUsernameText.startAnimation(slideFromLeft);
        confirmButton.startAnimation(slideFromLeft);

        Animation slideFromRight = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_right);
        slideFromRight.setDuration(300);
        newUsernameText.startAnimation(slideFromRight);
        newUsernameLabel.startAnimation(slideFromRight);
    }
}
