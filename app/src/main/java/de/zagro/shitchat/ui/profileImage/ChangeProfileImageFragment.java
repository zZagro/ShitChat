package de.zagro.shitchat.ui.profileImage;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentChangeProfileImageBinding;
import de.zagro.shitchat.ui.settings.SettingsFragment;

public class ChangeProfileImageFragment extends Fragment {

    public static final int PICK_IMAGE = 1;

    private ImageButton backButton;
    private ShapeableImageView userIcon;
    private ImageView userIconCircle;
    private MaterialButton camButton, galleryButton;

    FragmentChangeProfileImageBinding binding;

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            View view = getView();
            if (view != null)
            {
                Navigation.findNavController(view).navigateUp();
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setSharedElementEnterTransition(new ChangeBounds().setDuration(200));
        binding = FragmentChangeProfileImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = binding.backButton;
        userIcon = binding.userIcon;
        camButton = binding.cameraButton;
        galleryButton = binding.galleryButton;
        userIconCircle = binding.userIconCircle;

        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        playBottomNavAnimations(view);
        playAnimations();
        setUserIcon();
        onClick();
    }

    private void playAnimations()
    {
        Animation slideFromLeft = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_left);
        slideFromLeft.setDuration(200);
        camButton.startAnimation(slideFromLeft);

        Animation slideFromRight = AnimationUtils.loadAnimation(requireActivity(), R.anim.slidein_from_right);
        slideFromRight.setDuration(200);
        galleryButton.startAnimation(slideFromRight);
    }

    private void playBottomNavAnimations(View view)
    {
        Animation slideOutBottom = AnimationUtils.loadAnimation(requireActivity(), R.anim.slideout_bottom);
        slideOutBottom.setDuration(200);

        slideOutBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.getRootView().findViewById(R.id.nav_view).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.getRootView().findViewById(R.id.bottom_nav_line).startAnimation(slideOutBottom);
        view.getRootView().findViewById(R.id.nav_view).startAnimation(slideOutBottom);
    }

    private void onClick()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

        }
    }

    private void setUserIcon()
    {
        userIcon.setImageDrawable(Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name"));
    }
}
