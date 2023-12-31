package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.databinding.FragmentHomeBinding;

import java.sql.Timestamp;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding bind;
    private MainActivity activity;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    private int scrolledDistance = 0;
    private static final int HIDE_THRESHOLD = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        generateData();
    }

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
        bind.postsListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // dy > 0 means scrolling down
                // dy < 0 means scrolling up
                if (dy > 0) {
                    // Scrolling down
                    if (scrolledDistance > HIDE_THRESHOLD) {
                        hideLinearLayout1();
                        scrolledDistance = 0;
                    } else {
                        scrolledDistance += dy;
                    }
                } else {
                    // Scrolling up
                    if (Math.abs(scrolledDistance) > HIDE_THRESHOLD) {
                        showLinearLayout1();
                        scrolledDistance = 0;
                    } else {
                        scrolledDistance += dy;
                    }
                }
            }
        });
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
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa. Tortor aliquam nulla facilisi cras fermentum. Morbi tempus iaculis urna id volutpat lacus laoreet non.", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
    }
}