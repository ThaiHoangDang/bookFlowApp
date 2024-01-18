package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentAdminHomeBinding;
import com.rmit.bookflowapp.databinding.FragmentAuthenticationBinding;

public class AdminHomeFragment extends Fragment {
    private MainActivity activity;
    private FragmentAdminHomeBinding bind;

    public AdminHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentAdminHomeBinding.inflate(inflater, container, false);
        View view = bind.getRoot();

        activity.setBottomNavigationBarVisibility(true);
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }
}