package de.zagro.shitchat.ui.requests.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import de.ancash.shitchat.packet.user.RequestType;
import de.ancash.shitchat.user.FullUser;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.utils.OnSwipeTouchListener;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.templateUserImage.setImageDrawable(users.get(position).getDrawable());
        holder.templateUsername.setText(users.get(position).getName());

        View.OnClickListener showPopUp = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popUpBg = holder.itemView.getRootView().findViewById(R.id.requests_search_darken);
                popUpBg.setVisibility(View.VISIBLE);
                popUpBg.animate().alpha(0.75f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        popUpBg.animate().alpha(0.75f).setListener(null).start();
                    }
                });

                View popUp = holder.itemView.getRootView().findViewById(R.id.requests_search_popup);
                ConstraintLayout constraintLayout = (ConstraintLayout) popUp;
                popUp.setVisibility(View.VISIBLE);
                popUp.animate().alpha(1f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        popUp.animate().alpha(1f).setListener(null).start();
                    }
                });

                ShapeableImageView userImage = popUp.findViewById(R.id.requests_search_popup_image);
                userImage.setImageDrawable(users.get(position).getDrawable());

                TextView username = popUp.findViewById(R.id.requests_search_popup_username);
                username.setText(users.get(position).getName());

                MaterialButton friendRequestBtn = popUp.findViewById(R.id.requests_search_popup_friend_button);
                friendRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Future<Optional<String>> optional = SplashActivity.client.sendRequest(SplashActivity.client.searchUser(users.get(position).getName()).get().getFirst().get().get(0).getUserId(), RequestType.FRIEND);
                            if (optional.get().isPresent())
                            {
                                Toast.makeText(view.getContext(), "Failed to send Friend Request! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                            closePopUp(popUpBg, popUp);
                            Toast.makeText(view.getContext(), "Successfully sent Friend Request to " + users.get(position).getName(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "Failed to send Friend Request! Try Again.", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                });

                MaterialButton messageRequestBtn = popUp.findViewById(R.id.requests_search_popup_message_button);
                messageRequestBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Future<Optional<String>> optional = SplashActivity.client.sendRequest(SplashActivity.client.searchUser(users.get(position).getName()).get().getFirst().get().get(0).getUserId(), RequestType.MESSAGE);
                            if (optional.get().isPresent())
                            {
                                Toast.makeText(view.getContext(), "Failed to send Message Request! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                            closePopUp(popUpBg, popUp);
                            Toast.makeText(view.getContext(), "Successfully sent Message Request to " + users.get(position).getName(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), "Failed to send Message Request! Try Again.", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    }
                });

                ImageView exitBtn = popUp.findViewById(R.id.requests_search_popup_cross);
                exitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closePopUp(popUpBg, popUp);
                    }
                });
            }
        };

        holder.background.setOnClickListener(showPopUp);
    }

    private void closePopUp(View popUpBg, View popUp) {
        popUpBg.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                popUpBg.setVisibility(View.GONE);
                popUpBg.animate().alpha(0f).setListener(null).start();
            }
        });

        popUp.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                popUp.setVisibility(View.GONE);
                popUp.animate().alpha(0f).setListener(null).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView templateUsername;
        ImageView templateUserImage;
        View background;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            templateUsername = itemView.findViewById(R.id.requests_search_username);
            templateUserImage = itemView.findViewById(R.id.requests_search_image);
            background = itemView.findViewById(R.id.requests_search_bg);
        }
    }
}
