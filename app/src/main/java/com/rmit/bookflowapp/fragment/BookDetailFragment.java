package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.adapter.ReviewAdapter;
import com.rmit.bookflowapp.databinding.FragmentBookDetailBinding;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;

import java.sql.Timestamp;
import java.util.ArrayList;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private FragmentBookDetailBinding bind;
    private MainActivity activity;
    private ReviewAdapter reviewAdapter;
    private ArrayList<Review> reviews = new ArrayList<>();

    public BookDetailFragment() {
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
        bind = FragmentBookDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        // set up posts list
        reviewAdapter = new ReviewAdapter(activity, reviews);
        bind.bookDetailReviewList.setAdapter(reviewAdapter);
        bind.bookDetailReviewList.setLayoutManager(new LinearLayoutManager(activity));

        bind.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();  // Navigate back to the previous fragment
            }
        });

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void generateData() {
        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
    }
}