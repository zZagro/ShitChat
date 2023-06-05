package de.zagro.shitchat.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.templateIcon.setImageDrawable(users.get(position).getDrawable());
        holder.templateTime.setText(users.get(position).getTime());
        holder.backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
                buttonClick.setDuration(500);
                holder.backgroundView.startAnimation(buttonClick);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    ImageView templateIcon;
    TextView templateName, templateMessage, templateTime;
    View backgroundView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        templateIcon = itemView.findViewById(R.id.received_user_icon);
        templateName = itemView.findViewById(R.id.received_username);
        templateMessage = itemView.findViewById(R.id.received_message);
        templateTime = itemView.findViewById(R.id.received_time);
        backgroundView = itemView.findViewById(R.id.received_background);
    }
}

