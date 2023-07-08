package de.zagro.shitchat.ui.requests.outgoing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.zagro.shitchat.R;

public class RequestsOutgoingAdapter extends RecyclerView.Adapter<RequestsOutgoingAdapter.MyViewHolder>{

    private List<RequestOutgoingUser> users;

    public RequestsOutgoingAdapter(List<RequestOutgoingUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outgoing_user_layout, parent, false);
        return new RequestsOutgoingAdapter.MyViewHolder(view);
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
            templateUsername = itemView.findViewById(R.id.requests_outgoing_username);
            templateUserImage = itemView.findViewById(R.id.requests_outgoing_image);
        }
    }
}
