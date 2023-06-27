package de.zagro.shitchat.ui.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

//        if (position > 0)
//        {
//            if (messages.get(position - 1).isSent() != sent)
//            {
//                ConstraintLayout.LayoutParams receivedParams = (ConstraintLayout.LayoutParams) holder.messagesReceivedLayout.getRoot().getLayoutParams();
//                receivedParams.topMargin = 80;
//                holder.messagesReceivedLayout.getRoot().setLayoutParams(receivedParams);
//
//                ConstraintLayout.LayoutParams sentParams = (ConstraintLayout.LayoutParams) holder.messagesSentLayout.getRoot().getLayoutParams();
//                sentParams.topMargin = 80;
//                holder.messagesSentLayout.getRoot().setLayoutParams(sentParams);
//            }
//        }

        if (sent)
        {
            holder.messagesReceivedLayout.getRoot().setVisibility(View.INVISIBLE);
            holder.messagesSentLayout.getRoot().setVisibility(View.VISIBLE);

            holder.messagesSentLayout.messageSentTextContent.setText(currentMessageContent);
            holder.messagesSentLayout.messageSentTextTime.setText(currentMessageTime);

            Log.d("SENT", String.valueOf(holder.messagesSentLayout.getRoot().getVisibility()));
        }
        else
        {
            holder.messagesSentLayout.getRoot().setVisibility(View.INVISIBLE);
            holder.messagesReceivedLayout.getRoot().setVisibility(View.VISIBLE);

            holder.messagesReceivedLayout.messageReceivedTextContent.setText(currentMessageContent);
            holder.messagesReceivedLayout.messageReceivedTextTime.setText(currentMessageTime);

            Log.d("RECEIVED", String.valueOf(holder.messagesReceivedLayout.getRoot().getVisibility()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView messageSentContent, messageReceivedContent, messageSentTime, messageReceivedTime;
        MessageSentBinding messagesSentLayout;
        MessageReceivedBinding messagesReceivedLayout;
        public MyViewHolder(@NonNull MessagesBinding itemView) {
            super(itemView.getRoot());

            messagesSentLayout = itemView.messagesSentLayout;
            messagesReceivedLayout = itemView.messagesReceivedLayout;
        }
    }
}
