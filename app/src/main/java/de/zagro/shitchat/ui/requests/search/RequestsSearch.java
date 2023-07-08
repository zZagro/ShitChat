package de.zagro.shitchat.ui.requests.search;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.ancash.shitchat.user.User;
import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentRequestSearchBinding;
import de.zagro.shitchat.ui.home.RecyclerAdapter;

public class RequestsSearch extends Fragment {

    private FragmentRequestSearchBinding binding;

    private List<RequestSearchUser> users;
    private List<User> userData;
    String intentExtra = "Home";

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.putExtra("request", intentExtra);
            startActivity(intent);
            requireActivity().finish();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getArgs();
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        users = new ArrayList<>();
        userData = new ArrayList<>();

        showSearchedUsers(view, binding.requestSearchEdittext.getText().toString());
        checkInputUpdates(view);
    }

    private void findUsers(String input)
    {
        users.clear();

        userData = SplashActivity.client.searchUser(input).getFirst().get();

        for (int i = 0; i < userData.size(); i++)
        {
            RequestSearchUser searchUser = new RequestSearchUser(userData.get(i).getUsername(), Drawable.createFromStream(userData.get(i).getProfilePic().asStream(), "src name"));
            users.add(searchUser);
        }
    }

    private void addUsersSearched(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RequestsSearchAdapter recyclerAdapter = new RequestsSearchAdapter(users);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void showSearchedUsers(View view, String input)
    {
        findUsers(input);

        addUsersSearched(view.findViewById(R.id.requests_search_recylcerview));
    }

    private void checkInputUpdates(View view)
    {
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showSearchedUsers(view, binding.requestSearchEdittext.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.requestSearchEdittext.addTextChangedListener(tw);
    }

    private void getArgs()
    {
        if (requireActivity().getIntent() == null) return;
        if (!requireActivity().getIntent().getStringExtra("request").trim().isEmpty())
        {
            intentExtra = requireActivity().getIntent().getStringExtra("request");
        }
    }
}
