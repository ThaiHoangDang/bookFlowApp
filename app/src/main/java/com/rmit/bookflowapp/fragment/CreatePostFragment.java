package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.databinding.FragmentCreatePostBinding;
import com.rmit.bookflowapp.databinding.FragmentHomeBinding;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;

import java.util.ArrayList;

public class CreatePostFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentCreatePostBinding bind;
    private MainActivity activity;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts = new ArrayList<>();

    public CreatePostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentCreatePostBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }
}