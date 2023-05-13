package de.zagro.shitchat.ui.home;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.zagro.shitchat.R;
import de.zagro.shitchat.User;

public class HomeFragment extends Fragment {

    RecyclerView recyclerViewDirect;
    RecyclerView recyclerViewGroups;
    RecyclerAdapter recyclerAdapter;
    ArrayList<User> users = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        addToUsers();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showRecentMessages(view);
    }

    protected void addToUsers()
    {
        users.add(new User("John", "Hello, how are you?", R.drawable.user_pb_default));
        users.add(new User("Serena", "Hello, how are you?", R.drawable.user_pb_default));
        users.add(new User("Person1", "Hello, how are you?", R.drawable.user_pb_default));
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