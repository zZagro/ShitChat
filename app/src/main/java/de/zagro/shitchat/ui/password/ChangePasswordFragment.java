package de.zagro.shitchat.ui.password;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.ancash.shitchat.util.AuthenticationUtil;
import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentChangePasswordBinding;

public class ChangePasswordFragment extends Fragment {

    private ImageButton backButton;
    private TextView currentPasswordLabel, newPasswordLabel, confirmPasswordLabel;
    private EditText currentPasswordText, newPasswordText, confirmPasswordText;
    private ImageView viewCurrent, viewNew, viewConfirm;
    private MaterialButton confirmButton;

    private FragmentChangePasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = binding.passwordBackButton;
        confirmButton = binding.passwordBtnConfirm;

        currentPasswordLabel = binding.passwordCurrentLabel;
        newPasswordLabel = binding.passwordNewLabel;
        confirmPasswordLabel = binding.passwordConfirmLabel;

        currentPasswordText = binding.passwordCurrentText;
        newPasswordText = binding.passwordNewText;
        confirmPasswordText = binding.passwordConfirmText;

        viewCurrent = binding.passwordCurrentView;
        viewNew = binding.passwordNewView;
        viewConfirm = binding.passwordConfirmView;

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
                changePassword();
            }
        });

        viewCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentPasswordText.getTag().toString())
                {
                    case "password":
                        currentPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        currentPasswordText.setTag("text");
                        break;

                    case "text":
                        currentPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        currentPasswordText.setTag("password");
                        break;

                    default:
                        break;
                }
            }
        });

        viewNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (newPasswordText.getTag().toString())
                {
                    case "password":
                        newPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        newPasswordText.setTag("text");
                        break;

                    case "text":
                        newPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        newPasswordText.setTag("password");
                        break;

                    default:
                        break;
                }
            }
        });

        viewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (confirmPasswordText.getTag().toString())
                {
                    case "password":
                        confirmPasswordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        confirmPasswordText.setTag("text");
                        break;

                    case "text":
                        confirmPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPasswordText.setTag("password");
                        break;

                    default:
                        break;
                }
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPasswordText.clearFocus();
                newPasswordText.clearFocus();
                confirmPasswordText.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentPasswordText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(newPasswordText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(confirmPasswordText.getWindowToken(), 0);
            }
        });
    }

    private void changePassword()
    {
        if (!doPasswordsMatch())
        {
            Toast.makeText(requireActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPassword = currentPasswordText.getText().toString();
        String input = confirmPasswordText.getText().toString();
        String trimedInput = input.trim();

        if (TextUtils.isEmpty(trimedInput))
        {
            Toast.makeText(requireActivity(), "Nah, that's not gonna work my friend.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPasswordText.setText("");
        newPasswordText.setText("");
        confirmPasswordText.setText("");

        byte[] hashedPassword = AuthenticationUtil.hashPassword(SplashActivity.client.getEmail(), input.toCharArray());

        SplashActivity.client.changePassword(stringToPass(currentPassword), hashedPassword);

        SharedPreferences userDetails = requireActivity().getApplicationContext().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("hashedPassword",  passToString(hashedPassword));
        edit.apply();

        Toast.makeText(requireActivity(), "Password changed successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.putExtra("status", "Signup");
        startActivity(intent);
        requireActivity().finish();
    }

    private static byte[] stringToPass(String s) {
        byte[] b2 = new byte[s.split(" ").length];
        int i = 0;
        for(byte by : Stream.of(s.split(" ")).map(Byte::valueOf).collect(Collectors.toList()))
            b2[i++] = by;
        return b2;
    }

    private static String passToString(byte[] b) {
        return String.join(" ", IntStream.range(0, b.length).map(i -> b[i]).boxed().map(String::valueOf).collect(Collectors.toList()));
    }

    private boolean doPasswordsMatch()
    {
        return newPasswordText.getText().toString().trim().matches(confirmPasswordText.getText().toString().trim());
    }

    private void playAnimations()
    {
        Animation slideFromLeft = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_left);
        slideFromLeft.setDuration(300);
        currentPasswordLabel.startAnimation(slideFromLeft);
        currentPasswordText.startAnimation(slideFromLeft);
        confirmPasswordLabel.startAnimation(slideFromLeft);
        confirmPasswordText.startAnimation(slideFromLeft);

        Animation slideFromRight = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_right);
        slideFromRight.setDuration(300);
        newPasswordLabel.startAnimation(slideFromRight);
        newPasswordText.startAnimation(slideFromRight);
        confirmButton.startAnimation(slideFromRight);
    }

}
