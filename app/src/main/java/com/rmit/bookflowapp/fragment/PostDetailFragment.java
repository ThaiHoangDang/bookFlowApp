package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Genre;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentGenreBinding;
import com.rmit.bookflowapp.databinding.FragmentPostDetailBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PostDetailFragment extends Fragment {
    private static final String TAG = "PostDetailFragment";
    private FragmentPostDetailBinding bind;
    private MainActivity activity;
    private Post post;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        post = (Post) Objects.requireNonNull(arguments).getSerializable("POST_OBJECT");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentPostDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

}