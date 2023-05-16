package de.zagro.shitchat.ui.settings;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
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

        if (view.getRootView().findViewById(R.id.nav_view).getVisibility() == View.INVISIBLE)
        {
            playBottomSlideInNavAnimations(view);
        }

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
                FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder().addSharedElement(userIcon, "user_image").addSharedElement(userIconCircle, "user_image_frame").build();

                Navigation.findNavController(view).navigate(R.id.action_navigation_settings_to_changeProfileImageFragment, null, null, extras);
            }
        };

        userIcon.setOnClickListener(userIconClick);
        editUserIconBtn.setOnClickListener(userIconClick);
    }

    private void playBottomSlideInNavAnimations(View view)
    {
        Animation slideInBottom = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_bottom);
        slideInBottom.setDuration(200);
        slideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.getRootView().findViewById(R.id.bottom_nav_line).setVisibility(View.VISIBLE);
                view.getRootView().findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                ConstraintSet cs = new ConstraintSet();
//                ConstraintLayout layout = view.getRootView().findViewById(R.id.container);
//                cs.clone(layout);
//                cs.connect(R.id.nav_host_fragment_activity_main, ConstraintSet.BOTTOM, R.id.bottom_nav_line, ConstraintSet.TOP);
//                TransitionManager.beginDelayedTransition(layout);
//                cs.applyTo(layout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.getRootView().findViewById(R.id.bottom_nav_line).startAnimation(slideInBottom);
        view.getRootView().findViewById(R.id.nav_view).startAnimation(slideInBottom);
    }

}
