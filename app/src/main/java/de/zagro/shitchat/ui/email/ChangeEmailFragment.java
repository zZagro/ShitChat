package de.zagro.shitchat.ui.email;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentChangeEmailBinding;

public class ChangeEmailFragment extends Fragment {

    private ImageButton backButton;
    private TextView currentEmailText, currentEmailLabel, newEmailLabel;
    private EditText newEmailText;
    private MaterialButton confirmButton;

    private FragmentChangeEmailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentEmailText = binding.emailCurrentText;
        currentEmailLabel = binding.emailCurrentLabel;
        newEmailLabel = binding.emailNewLabel;
        newEmailText = binding.emailNewText;
        confirmButton = binding.emailBtnConfirm;
        backButton = binding.emailBackButton;

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
    }

    private void setCurrentUsername()
    {
        currentEmailText.setText(SplashActivity.client.getUser().getName());
    }

    private void playAnimations()
    {
        Animation slideFromLeft = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_left);
        slideFromLeft.setDuration(300);
        currentEmailLabel.startAnimation(slideFromLeft);
        currentEmailText.startAnimation(slideFromLeft);
        confirmButton.startAnimation(slideFromLeft);

        Animation slideFromRight = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_right);
        slideFromRight.setDuration(300);
        newEmailText.startAnimation(slideFromRight);
        newEmailLabel.startAnimation(slideFromRight);
    }

}
