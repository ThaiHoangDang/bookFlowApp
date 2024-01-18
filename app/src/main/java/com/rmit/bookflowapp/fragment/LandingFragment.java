package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentAuthenticationBinding;

public class LandingFragment extends Fragment {
    private FirebaseAuth mAuth;
    private MainActivity activity;
    private LandingFragment bind;

    public LandingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        System.out.println(mAuth.getCurrentUser().getEmail());
        if (mAuth.getCurrentUser().getEmail().equals("admin@admin.com")) {
            activity.navController.navigate(R.id.adminHomeFragment);
        } else if (mAuth.getCurrentUser() == null) {
            activity.navController.navigate(R.id.authenticationFragment);
        } else {
            activity.navController.navigate(R.id.homeFragment);
        }
        return inflater.inflate(R.layout.fragment_landing, container, false);
    }
}