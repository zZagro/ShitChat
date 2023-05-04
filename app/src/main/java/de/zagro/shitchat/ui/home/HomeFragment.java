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
import androidx.fragment.app.Fragment;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.zagro.shitchat.R;

public class HomeFragment extends Fragment {

    //private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//
//        View root = binding.getRoot();
//        return root;

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        showRecentMessages(view);

        return view;
    }

    private void showRecentMessages(View rootLayout)
    {
        LinearLayout homeLayout = (LinearLayout) rootLayout.findViewById(R.id.home_view);
        TextView recentDirectText = (TextView) homeLayout.findViewById(R.id.home_recent_direct_text);
        TextView recentGroupsText = (TextView) homeLayout.findViewById(R.id.home_recent_groups_text);
        homeLayout.removeAllViews();

        addRecentMessages(homeLayout, recentDirectText, recentDirectMessagesAmount());
        addRecentMessages(homeLayout, recentGroupsText, recentGroupMessagesAmount());
    }

    private void addRecentMessages(LinearLayout homeView, TextView title, int messageAmount)
    {
        homeView.addView(title);

        if (messageAmount > 0)
        {
            for (int i = 0; i < messageAmount; i++)
            {
                View recentTemplate = LayoutInflater.from(getContext()).inflate(R.layout.recent_message, homeView);
                ImageView templateIcon = recentTemplate.findViewById(R.id.recent_icon);
                TextView templateName = recentTemplate.findViewById(R.id.recent_name);
                TextView templateMessage = recentTemplate.findViewById(R.id.recent_message);
                TextView templateDate = recentTemplate.findViewById(R.id.recent_date);
                TextView templateTime = recentTemplate.findViewById(R.id.recent_time);
                switch (i)
                {
                    case 0:
                        setText(templateName, String.valueOf(i));
                        setText(templateMessage, String.valueOf(i));
                    case 1:
                        setText(templateName, "Apple");
                        setText(templateMessage, "I like apples!");
                    case 2:
                        setText(templateName, "dfskufhsjkdfhg");
                        setText(templateMessage, "WTF is that name?");
                }
            }
        }
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
        //binding = null;
    }
}