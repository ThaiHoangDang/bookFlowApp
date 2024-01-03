package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentBookDetailBinding;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private FragmentBookDetailBinding bind;
    private MainActivity activity;

    public BookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentBookDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }
}