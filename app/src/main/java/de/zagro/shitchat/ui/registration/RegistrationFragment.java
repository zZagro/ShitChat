package de.zagro.shitchat.ui.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import de.zagro.shitchat.R;

public class RegistrationFragment extends Fragment {

    EditText email, username, password;

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.registration_email);
        username = view.findViewById(R.id.registration_username);
        password = view.findViewById(R.id.registration_password);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

//    private void inputFocus()
//    {
//        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//
//            }
//        });
//    }
}
