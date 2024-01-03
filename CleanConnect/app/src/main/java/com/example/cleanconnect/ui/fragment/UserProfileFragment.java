package com.example.cleanconnect.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentUserProfileBinding;
import com.example.cleanconnect.model.User;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.ImageLoaderUtils;
import com.example.cleanconnect.viewmodel.UserProfileViewModel;
import com.example.cleanconnect.viewmodel.factory.UserProfileViewModelFactory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;
    private MainActivity activity;
    private AppBarLayout appBarLayout;
    private MaterialToolbar materialToolbar;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        UserProfileViewModelFactory factory = new UserProfileViewModelFactory(activity.getUser().getUid());
        viewModel = new ViewModelProvider(this, factory).get(UserProfileViewModel.class);

        // Observe changes in user profile data
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                updateUI(user);
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                activity.navController.navigate(R.id.action_userProfileFragment_to_loginFragment);
            }
        });
        initAppBar();
    }

    private void updateUI(User user) {
        // Update UI elements with user profile data
        binding.textViewDisplayName.setText(user.getDisplayName());
        binding.textViewEmail.setText(user.getEmail());
        binding.textViewPhone.setText(user.getPhoneNumber());
        ImageLoaderUtils loader = new ImageLoaderUtils();
        loader.loadImage(binding.imageView, user.getProfileImageUrl());
    }

    private void initAppBar() {
        appBarLayout = binding.getRoot().findViewById(R.id.toolbar_fragment);
        materialToolbar = binding.getRoot().findViewById(R.id.toolbar);

        activity.setSupportActionBar(materialToolbar);
        Objects.requireNonNull(materialToolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));
    }
}
