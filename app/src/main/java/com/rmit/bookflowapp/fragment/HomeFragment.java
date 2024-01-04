package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.databinding.FragmentHomeBinding;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding bind;
    private MainActivity activity;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private int scrolledDistance = 0;
    private static final int HIDE_THRESHOLD = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generateData();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        // set up posts list
        postAdapter = new PostAdapter(activity, posts);
        bind.postsListView.setAdapter(postAdapter);
        bind.postsListView.setLayoutManager(new LinearLayoutManager(activity));

        // Add scroll listener to RecyclerView
        bind.postsListView.setOnTouchListener(new TranslateAnimationUtil(activity, bind.linearlayout1));
        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void hideLinearLayout1() {
        bind.linearlayout1.animate().translationY(-bind.linearlayout1.getHeight()).setDuration(100).start();
    }

    private void showLinearLayout1() {
        bind.linearlayout1.animate().translationY(0).setDuration(100).start();
    }

    private void generateData() {
    }
}