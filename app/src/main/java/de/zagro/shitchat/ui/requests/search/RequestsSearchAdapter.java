package de.zagro.shitchat.ui.requests.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.zagro.shitchat.R;

public class RequestsSearchAdapter extends RecyclerView.Adapter<RequestsSearchAdapter.MyViewHolder> {

    private List<RequestSearchUser> users;

    public RequestsSearchAdapter(List<RequestSearchUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.templateUserImage.setImageDrawable(users.get(position).getDrawable());
        holder.templateUsername.setText(users.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView templateUsername;
        ImageView templateUserImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            templateUsername = itemView.findViewById(R.id.requests_search_username);
            templateUserImage = itemView.findViewById(R.id.requests_search_image);
        }
    }
}
