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
import java.util.Set;

import de.ancash.shitchat.user.FullUser;
import de.ancash.shitchat.user.User;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentRequestsIncomingBinding;

public class RequestsIncoming extends Fragment {

    private FragmentRequestsIncomingBinding binding;

    private List<User> friendIncomingListRaw;
    private List<RequestsIncomingUser> friendIncomingList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsIncomingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendIncomingListRaw = new ArrayList<>();
        friendIncomingList = new ArrayList<>();

        showIncomingRequests();
    }

    private void getIncomingFriendRequests()
    {
        friendIncomingListRaw.clear();
        friendIncomingList.clear();

        FullUser user = (FullUser) SplashActivity.client.getUser();
        Set<User> incoming = user.getFriendList().getIncoming();
        friendIncomingListRaw.addAll(incoming);

        for (User u : friendIncomingListRaw)
        {
            friendIncomingList.add(new RequestsIncomingUser(u.getUsername(), Drawable.createFromStream(u.getProfilePic().asStream(), "src name")));
        }
    }

    private void showIncomingRequests()
    {
        getIncomingFriendRequests();
        RecyclerView recyclerView = binding.requestsIncomingRecylcerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RequestsIncomingAdapter recyclerAdapter = new RequestsIncomingAdapter(friendIncomingList);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
