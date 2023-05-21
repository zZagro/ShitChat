package de.zagro.shitchat.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.ancash.shitchat.ShitChatImage;
import de.ancash.shitchat.client.ShitChatClient;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.User;
import de.zagro.shitchat.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewDirect;
    RecyclerView recyclerViewGroups;
    RecyclerAdapter recyclerAdapter;
    ArrayList<User> users = new ArrayList<>();

    FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addToUsers();
        showRecentMessages(view);
        onClick();
    }

    private void onClick()
    {
        View.OnClickListener directMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_direct);
            }
        };

        View.OnClickListener groupsMessageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.navigation_groups);
            }
        };

        binding.homeRecentDirectText.setOnClickListener(directMessageListener);
        binding.directArrow.setOnClickListener(directMessageListener);

        binding.homeRecentGroupsText.setOnClickListener(groupsMessageListener);
        binding.groupsArrow.setOnClickListener(groupsMessageListener);
    }

    protected void addToUsers()
    {
        users.clear();
        users.add(new User("John", "Hello, how are you? I don't like being here. Please get me out of here!!", "12:06", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new User("Serena", "Hello, how are you?", "10:12", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
        users.add(new User("Person1", "Hello, how are you?", "21:01", Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name")));
    }

    private void showRecentMessages(View view)
    {
        recyclerViewDirect = view.findViewById(R.id.recent_recycler_direct);
        recyclerViewGroups = view.findViewById(R.id.recent_recycler_groups);

        addRecentMessages(recyclerViewDirect);
        addRecentMessages(recyclerViewGroups);
    }

    private void addRecentMessages(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAdapter = new RecyclerAdapter(users);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private int recentDirectMessagesAmount()
    {
        return 3;
    }

    private int recentGroupMessagesAmount()
    {
        return 3;
    }

    private void setText(TextView view, String item) {
        view.setText(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}