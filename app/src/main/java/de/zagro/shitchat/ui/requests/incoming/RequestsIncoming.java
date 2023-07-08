package de.zagro.shitchat.ui.requests.incoming;

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

import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentRequestsIncomingBinding;
import de.zagro.shitchat.ui.requests.outgoing.RequestOutgoingUser;
import de.zagro.shitchat.ui.requests.outgoing.RequestsOutgoingAdapter;

public class RequestsIncoming extends Fragment {

    private FragmentRequestsIncomingBinding binding;

    private List<RequestsIncomingUser> users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsIncomingBinding.inflate(inflater, container, false);
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

        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new RequestsIncomingUser("Zagro", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));

    }

    private void showOutgoingRequests()
    {
        getUsers();
        RecyclerView recyclerView = binding.requestsIncomingRecylcerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RequestsIncomingAdapter recyclerAdapter = new RequestsIncomingAdapter(users);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
