package de.zagro.shitchat.ui.profileImage;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

import de.zagro.shitchat.R;
import de.zagro.shitchat.SplashActivity;
import de.zagro.shitchat.databinding.FragmentUserImageAreaBinding;

public class ProfileImagePickerFragment extends Fragment {

    private ImageView userImage;

    private ImageView rectangleOutline;

    private float x, y, maxX, maxY, actualHeight, actualWidth;

    private MaterialButton cancelButton, confirmButton;

    private FragmentUserImageAreaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserImageAreaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rectangleOutline = binding.rectangleOutline;
        userImage = binding.userImage;
        cancelButton = binding.cancelButton;
        confirmButton = binding.confirmButton;

        setImageSize();
        getImageSize(view);
        onClick();
    }

    private void setImageSize()
    {
        userImage.setImageDrawable(Drawable.createFromStream(SplashActivity.client.getUser().getProfilePic().asStream(), "src name"));
    }

    private void getImageSize(View view)
    {
        userImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                userImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                actualHeight = userImage.getDrawable().getIntrinsicHeight();
                actualWidth = userImage.getDrawable().getIntrinsicWidth();

                ConstraintSet cs = new ConstraintSet();
                ConstraintLayout layout = (ConstraintLayout) view;
                cs.clone(layout);
                cs.setDimensionRatio(R.id.user_image, actualWidth + ":" + actualHeight);
                Log.i("HEIGHT FINAL", String.valueOf(actualHeight));
                Log.i("HEIGHT MAX", String.valueOf(userImage.getHeight()));
                Log.i("HEIGHT PERCENT", String.valueOf(actualHeight / userImage.getHeight()));
                cs.applyTo(layout);

                maxX = userImage.getWidth();
                maxY = userImage.getHeight();
            }
        });
    }

    private void chooseImageArea()
    {
        float left = x / maxX;
        float top = y /  maxY;

        Log.i("LEFT", String.valueOf(left));
        Log.i("TOP", String.valueOf(top));

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rectangleOutline.getLayoutParams();
        params.verticalBias = (top < 0) ? 0 : top;
        params.horizontalBias = (left < 0) ? 0 : left;
        rectangleOutline.setLayoutParams(params);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onClick()
    {
        userImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                x = event.getX();
                y = event.getY();

                chooseImageArea();

                return true;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }
}
