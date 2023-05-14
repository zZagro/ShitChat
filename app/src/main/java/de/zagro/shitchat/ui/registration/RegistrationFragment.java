package de.zagro.shitchat.ui.registration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Optional;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.util.AuthenticationUtil;
import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentLoginBinding;
import de.zagro.shitchat.databinding.FragmentRegistrationBinding;

public class RegistrationFragment extends Fragment {

    Button registerButton;
    EditText username, email, password;

    TextView goToLoginText, titleText;

    ImageView showPasswordBtn;

    FragmentRegistrationBinding binding;

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            View view = getView();
            if (view != null) Navigation.findNavController(view).navigateUp();
        }
    };

    public RegistrationFragment()
    {
        super(R.layout.fragment_registration);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerButton = binding.registerButton;
        username = binding.registerUsername;
        email = binding.registerEmail;
        password = binding.registerPassword;
        goToLoginText = binding.goToLoginText;
        showPasswordBtn = binding.viewPasswordIcon;
        titleText = binding.splashTitle;

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        changeTextColorTitle();
        changeTextColorLogIn();
        onClick();
    }

    private void registerUser()
    {
        if (password.length() < 8)
        {
            Toast.makeText(requireActivity(), "Password has to be at least 8 Character Long!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValid(email.getText()))
        {
            Toast.makeText(requireActivity(), "Email is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() < 1)
        {
            Toast.makeText(requireActivity(), "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Optional<String> optional = SplashActivity.client.signIn(email.getText().toString(), AuthenticationUtil.hashPassword(email.getText().toString(), password.getText().toString().toCharArray()), username.getText().toString());
        if (optional.isPresent())
        {
            String errorMessage = optional.get();
            if (errorMessage.equals(ShitChatPlaceholder.INTERNAL_ERROR))
                Toast.makeText(requireActivity(), "Something went wrong! Try again later!", Toast.LENGTH_SHORT).show();
            if (errorMessage.equals(ShitChatPlaceholder.ACCOUNT_ALREADY_EXISTS))
                Toast.makeText(requireActivity(), "The Email is already in use!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.putExtra("status", "Signup");
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void changeTextColorTitle()
    {
        SpannableString string = new SpannableString(getString(R.string.app_title));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(requireActivity().getColor(R.color.primary_purple));
        string.setSpan(foregroundColorSpan, 8, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        titleText.setText(string);
    }

    private void changeTextColorLogIn()
    {
        SpannableString string = new SpannableString(getString(R.string.goToLoginText));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(requireActivity().getColor(R.color.primary_indigo));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Navigation.findNavController(view).navigateUp();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                goToLoginText.setHighlightColor(Color.TRANSPARENT);
            }
        };

        string.setSpan(clickableSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        goToLoginText.setMovementMethod(LinkMovementMethod.getInstance());
        string.setSpan(foregroundColorSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(boldSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        goToLoginText.setText(string);
    }

    private void onClick()
    {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        showPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (password.getTag().toString())
                {
                    case "password":
                        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        password.setTag("text");
                        break;

                    case "text":
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setTag("password");
                        break;

                    default:
                        break;
                }
            }
        });

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.clearFocus();
                username.clearFocus();
                password.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
