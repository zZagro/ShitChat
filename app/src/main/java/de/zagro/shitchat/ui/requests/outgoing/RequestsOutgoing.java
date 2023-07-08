package de.zagro.shitchat.ui.requests.outgoing;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentRequestsOutgoingBinding;
import de.zagro.shitchat.ui.requests.search.RequestSearchUser;
import de.zagro.shitchat.ui.requests.search.RequestsSearchAdapter;

public class RequestsOutgoing extends Fragment {

    private FragmentRequestsOutgoingBinding binding;

    private List<RequestOutgoingUser> users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsOutgoingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        users = new ArrayList<>();

        showOutgoingRequests();
    }

    private void getUsers()
    {
        users.clear();

        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestOutgoingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));

    }

    private void showOutgoingRequests()
    {
        getUsers();
        RecyclerView recyclerView = binding.requestsOutgoingRecylcerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RequestsOutgoingAdapter recyclerAdapter = new RequestsOutgoingAdapter(users);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
