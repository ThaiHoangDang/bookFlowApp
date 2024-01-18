package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentMoreBinding;
import com.rmit.bookflowapp.databinding.FragmentUserProfileBinding;
import com.rmit.bookflowapp.repository.UserRepository;
import com.squareup.picasso.Picasso;

public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";
    private FragmentUserProfileBinding binding;
    private MainActivity activity;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigateUp();
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserRepository.getInstance().getUserById(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                initView();
            }
        });
        return binding.getRoot();
    }

    public void initView() {
        if (user.getImageId() != null && !user.getImageId().isEmpty()) {
            Picasso.get().load(user.getImageId()).into(binding.imageView);
            Log.d(TAG, user.getImageId());
        }
        binding.textViewDisplayName.setText(user.getName());
        binding.textViewEmail.setText(user.getEmail());
        binding.textViewRole.setText(user.getRole());
    }
}