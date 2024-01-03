package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.databinding.FragmentHomeBinding;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;

import java.util.ArrayList;


public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";
    private FragmentLibraryBinding bind;
    private MainActivity activity;
    private static final int HIDE_THRESHOLD = 0;

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentLibraryBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }
}