package de.zagro.shitchat.ui.direct;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.zagro.shitchat.R;
import de.zagro.shitchat.databinding.FragmentDirectBinding;

public class DirectFragment extends Fragment {

    private FragmentDirectBinding binding;

    private boolean isSearchVisible = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDirectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.directSearchEdittext.setFocusable(false);

        toggleSearchBar();
    }

    private void toggleSearchBar()
    {
        View.OnClickListener showSearchBar = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.viewDirectSearchBg.getLayoutParams();
                layoutParams.setMargins(0, 22, 0, 0);
                binding.viewDirectSearchBg.setLayoutParams(layoutParams);

                ConstraintLayout constraintLayout = (ConstraintLayout) view.getParent();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                if (isSearchVisible)
                {
                    constraintSet.clear(R.id.view_direct_search_bg, ConstraintSet.TOP);
                    constraintSet.connect(R.id.view_direct_search_bg, ConstraintSet.BOTTOM, R.id.view_direct_top, ConstraintSet.BOTTOM);
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    constraintSet.applyTo(constraintLayout);

                    binding.directSearchEdittext.setFocusable(false);

                    isSearchVisible = false;
                }
                else
                {
                    constraintSet.connect(R.id.view_direct_search_bg, ConstraintSet.TOP, R.id.view_direct_top, ConstraintSet.BOTTOM);
                    constraintSet.clear(R.id.view_direct_search_bg, ConstraintSet.BOTTOM);
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    constraintSet.applyTo(constraintLayout);

                    binding.directSearchEdittext.setFocusableInTouchMode(true);

                    isSearchVisible = true;
                }
            }
        };

        binding.directSearch.setOnClickListener(showSearchBar);
    }
}
