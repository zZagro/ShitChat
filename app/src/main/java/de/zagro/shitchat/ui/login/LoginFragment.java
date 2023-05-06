package de.zagro.shitchat.ui.login;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;

public class LoginFragment extends Fragment {

    Button loginButton;

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginButton = view.findViewById(R.id.login_button);
        onClick();
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
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("name", "John");
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
}
