package de.zagro.shitchat.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.zagro.shitchat.databinding.FragmentGroupsBinding;

public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GroupsViewModel groupsViewModel = new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGroups;
        groupsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
}
