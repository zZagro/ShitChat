package de.zagro.shitchat.ui.login;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.ancash.shitchat.ShitChatPlaceholder;
import de.ancash.shitchat.util.AuthenticationUtil;
import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentDirectBinding;
import de.zagro.shitchat.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    Button loginButton;
    EditText email, password;

    TextView goToRegisterText, titleText;

    ImageView showPasswordBtn;

    FragmentLoginBinding binding;

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            View view = getView();
            if (view != null) requireActivity().finish();
        }
    };

    public LoginFragment()
    {
        super(R.layout.fragment_login);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton = binding.loginButton;
        email = binding.loginEmail;
        password = binding.loginPassword;
        goToRegisterText = binding.goToRegisterText;
        showPasswordBtn = binding.viewPasswordIcon;
        titleText = binding.splashTitle;

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        changeTextColorTitle();
        changeTextColorSignUp();
        onClick();
    }

    private void loginUser()
    {
        if (!isEmailValid(email.getText()))
        {
            Toast.makeText(requireActivity(), "Email is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 1)
        {
            Toast.makeText(requireActivity(), "Please enter your Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] hashedPassword = AuthenticationUtil.hashPassword(email.getText().toString(), password.getText().toString().toCharArray());

        Optional<String> optional = SplashActivity.client.login(email.getText().toString(), hashedPassword);
        if (optional.isPresent())
        {
            String errorMessage = optional.get();

            SplashActivity.client.sendErrorMessages(errorMessage, requireActivity());
        }
        else
        {
            SharedPreferences userDetails = requireActivity().getApplicationContext().getSharedPreferences("userdetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit();
            edit.putString("email", email.getText().toString());
            edit.putString("hashedPassword",  passToString(hashedPassword));
            edit.apply();

            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.putExtra("status", "Login");
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private static String passToString(byte[] b) {
        return String.join(" ", IntStream.range(0, b.length).map(i -> b[i]).boxed().map(String::valueOf).collect(Collectors.toList()));
    }

    private void changeTextColorTitle()
    {
        SpannableString string = new SpannableString(getString(R.string.app_title));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(requireActivity().getColor(R.color.primary_purple));
        string.setSpan(foregroundColorSpan, 8, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        titleText.setText(string);
    }

    private void changeTextColorSignUp()
    {
        SpannableString string = new SpannableString(getString(R.string.don_t_have_an_account_yet_sign_up));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(requireActivity().getColor(R.color.primary_indigo));
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                goToRegisterText.setHighlightColor(Color.TRANSPARENT);
            }
        };

        string.setSpan(clickableSpan, 27, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        goToRegisterText.setMovementMethod(LinkMovementMethod.getInstance());
        string.setSpan(foregroundColorSpan, 27, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(boldSpan, 27, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        goToRegisterText.setText(string);
    }

    private void onBackPressed()
    {
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        onBackPressed();
    }

    private void onClick()
    {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
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
                password.clearFocus();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
