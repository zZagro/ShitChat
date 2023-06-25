package de.zagro.shitchat.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.zagro.shitchat.R;
import de.zagro.shitchat.databinding.FragmentChatBinding;

public class Chat extends Fragment {

    private MaterialButton sendButton;
    private EditText inputText;
    private MaterialCardView bottomNavBg;
    private FragmentChatBinding binding;

    private List<Message> messageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputText = binding.inputBottomChat;
        bottomNavBg = binding.barChatBottom;
        sendButton = binding.sendBottomChat;

        getArgs();

        messageList = new ArrayList<>();
        addMessages();

//        ImageView imageView = new ImageView(requireActivity());
//        imageView.setImageResource(R.drawable.circle_green);
//        ((ConstraintLayout) view).addView(imageView);
    }

    private void addMessages()
    {
        messageList.add(new Message("Hello, how are you?", "12:02", true));
        messageList.add(new Message("I'm fine?", "12:03", false));
        messageList.add(new Message("What are you doing?", "12:03", false));
        messageList.add(new Message("Nothing", "12:04", true));
        messageList.add(new Message("dfslkfjsdlkfjs dsljkfns,dkfj sdflksjfddlsk dsfkjfflkdsjfsdfj sdflksjdfldskjf", "12:05", true));

        loadMessages();
    }

    private void loadMessages()
    {
        MessageAdapter messageAdapter = new MessageAdapter(messageList);
        binding.recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
    }

//    private void loadMessages(int amount)
//    {
//        if(amount > previousMessages.size()) amount = previousMessages.size();
//
//        for (int i = amount; i < previousMessages.size(); i++)
//        {
//            Object current = previousMessages.keySet().toArray()[i];
//            if (current.toString().matches("Self"))
//            {
//
//            }
//            else if (current.toString().matches("Other"))
//            {
//
//            }
//        }
//    }

    private void sendMessage()
    {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getMessage().matches("")) return;
                String message = getMessage();
                inputText.setText("");
            }
        });
    }

    private String getMessage()
    {
        return inputText.getText().toString();
    }

    private void getArgs()
    {
        if (requireActivity().getIntent() == null) return;

        binding.chatUsername.setText(requireActivity().getIntent().getStringExtra("user"));
    }
}
