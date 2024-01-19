package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentMessageListBinding;
import com.rmit.bookflowapp.databinding.FragmentMoreBinding;

import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment {
    private String TAG = "MoreFragment";
    private FragmentMoreBinding binding;
    private MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);

        binding.userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
            }
        });
        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "follow", Toast.LENGTH_SHORT).show();
            }
        });
        binding.aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigate(R.id.aboutFragment);
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
//                activity.navController.navigate(R.id.authenticationFragment);
                activity.navController.navigate(R.id.action_moreFragment_to_authenticationFragment);
            }
        });
        return binding.getRoot();
    }
}