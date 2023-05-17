package de.zagro.shitchat.ui.settings;

import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import com.google.android.material.button.MaterialButton;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.UserImageActivity;
import de.zagro.shitchat.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private ImageView userIcon, userIconCircle;
    private MaterialButton editUserIconBtn;

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSharedElementEnterTransition(new ChangeBounds().setDuration(200));
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userIcon = binding.userIcon;
        editUserIconBtn = binding.userIconEditButton;
        userIconCircle = binding.userIconCircle;

        setUserIcon();
        editUserIcon();
    }

    private void setUserIcon()
    {
        userIcon.setImageDrawable(Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name"));
    }

    private void editUserIcon()
    {
        View.OnClickListener userIconClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UserImageActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), Pair.create(userIcon, "user_image"), Pair.create(userIconCircle, "user_image_frame"));
                startActivity(intent, options.toBundle());
            }
        };

        userIcon.setOnClickListener(userIconClick);
        editUserIconBtn.setOnClickListener(userIconClick);
    }

}
