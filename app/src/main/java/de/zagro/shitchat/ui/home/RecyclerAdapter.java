package de.zagro.shitchat.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.zagro.shitchat.R;
import de.zagro.shitchat.User;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    List<User> users;

    public RecyclerAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.templateName.setText(users.get(position).getName());
        holder.templateMessage.setText(users.get(position).getMessage());
        holder.templateIcon.setImageResource(users.get(position).getDrawable());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    ImageView templateIcon;
    TextView templateName;
    TextView templateMessage;
//    TextView templateDate;
//    TextView templateTime;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        templateIcon = itemView.findViewById(R.id.recent_icon);
        templateName = itemView.findViewById(R.id.recent_name);
        templateMessage = itemView.findViewById(R.id.recent_message);
//        templateDate = itemView.findViewById(R.id.recent_date);
//        templateTime = itemView.findViewById(R.id.recent_time);
    }
}

