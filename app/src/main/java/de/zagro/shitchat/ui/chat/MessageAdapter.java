package de.zagro.shitchat.ui.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.zagro.shitchat.databinding.MessageReceivedBinding;
import de.zagro.shitchat.databinding.MessageSentBinding;
import de.zagro.shitchat.databinding.MessagesBinding;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private List<Message> messages = new ArrayList<>();

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(MessagesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        boolean sent = messages.get(position).isSent();
        String currentMessageContent = messages.get(position).getMessage();
        String currentMessageTime = messages.get(position).getTime();

        if (position > 0)
        {
            if (messages.get(position - 1).isSent() != sent)
            {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.messagesReceivedLayout.getRoot().getRootView().getLayoutParams();
                layoutParams.topMargin = 80;
                holder.messagesReceivedLayout.getRoot().getRootView().setLayoutParams(layoutParams);
            }
        }

        if (sent)
        {
            holder.messagesReceivedLayout.getRoot().setVisibility(View.GONE);
            holder.messagesSentLayout.getRoot().setVisibility(View.VISIBLE);

            holder.messagesSentLayout.messageSentTextContent.setText(currentMessageContent);
            holder.messagesSentLayout.messageSentTextTime.setText(currentMessageTime);
        }
        else
        {
            holder.messagesSentLayout.getRoot().setVisibility(View.GONE);
            holder.messagesReceivedLayout.getRoot().setVisibility(View.VISIBLE);

            holder.messagesReceivedLayout.messageReceivedTextContent.setText(currentMessageContent);
            holder.messagesReceivedLayout.messageReceivedTextTime.setText(currentMessageTime);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        MessageSentBinding messagesSentLayout;
        MessageReceivedBinding messagesReceivedLayout;
        public MyViewHolder(@NonNull MessagesBinding itemView) {
            super(itemView.getRoot());

            messagesSentLayout = itemView.messagesSentLayout;
            messagesReceivedLayout = itemView.messagesReceivedLayout;
        }
    }
}
