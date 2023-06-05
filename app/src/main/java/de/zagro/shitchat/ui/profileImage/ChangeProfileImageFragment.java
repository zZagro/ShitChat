package de.zagro.shitchat.ui.profileImage;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Toast;

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
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.zagro.shitchat.MainActivity;
import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.UserImageActivity;
import de.zagro.shitchat.databinding.FragmentChangeProfileImageBinding;
import de.zagro.shitchat.ui.settings.SettingsFragment;

public class ChangeProfileImageFragment extends Fragment {

    public static final int PICK_IMAGE = 1;

    private ImageButton backButton;
    private ShapeableImageView userIcon;
    private ImageView userIconCircle;
    private MaterialButton camButton, galleryButton;

    private Uri selectedImgUri;

    FragmentChangeProfileImageBinding binding;

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

    private void onClick()
    {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
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
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP)
        {
            try {
                Uri imageUriResultCrop = UCrop.getOutput(data);
                final InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUriResultCrop);
                final Bitmap userImage = BitmapFactory.decodeStream(imageStream);

                Drawable d = new BitmapDrawable(getResources(), userImage);

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("status", "Signup");
                startActivity(intent);
                requireActivity().finish();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.i("ERROR", cropError.getMessage());
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                selectedImgUri = data.getData();
                final InputStream imageStream = requireActivity().getContentResolver().openInputStream(selectedImgUri);
                final Bitmap userImage = BitmapFactory.decodeStream(imageStream);

                startCrop();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void startCrop()
    {
        UCrop uCrop = UCrop.of(selectedImgUri, Uri.fromFile(new File(requireActivity().getCacheDir(), "CroppedImage.png")));

        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(450, 450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(requireActivity(), this, UCrop.REQUEST_CROP);
    }

    private UCrop.Options getCropOptions()
    {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(100);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);

        options.setStatusBarColor(getResources().getColor(R.color.background_color));
        options.setToolbarColor(getResources().getColor(R.color.primary_indigo));

        return options;
    }

    private void setUserIcon()
    {
        userIcon.setImageDrawable(Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name"));
    }
}
