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

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void generateData() {
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
        posts.add(new Review("Cuốn sách rất hay và ý nghĩa!", "Hoang Dang", new Timestamp(System.currentTimeMillis()), "De Men", 5));
    }
}