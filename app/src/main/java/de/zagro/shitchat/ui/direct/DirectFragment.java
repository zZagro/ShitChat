package de.zagro.shitchat.ui.direct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.zagro.shitchat.R;
import de.zagro.shitchat.databinding.FragmentDirectBinding;

public class DirectFragment extends Fragment {

    private FragmentDirectBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DirectViewModel directViewModel = new ViewModelProvider(this).get(DirectViewModel.class);

        binding = FragmentDirectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDirect;
        directViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
