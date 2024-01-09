package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ReviewAdapter;
import com.rmit.bookflowapp.databinding.FragmentBookDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private FragmentBookDetailBinding bind;
    private MainActivity activity;
    private ReviewAdapter reviewAdapter;
    private Book book;
    private ArrayList<Review> reviews = new ArrayList<>();

    public BookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();

        book = (Book) Objects.requireNonNull(arguments).getSerializable("BOOK_OBJECT");

        generateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentBookDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        setupView(book);

        reviewAdapter = new ReviewAdapter(activity, reviews);
        bind.bookDetailReviewList.setAdapter(reviewAdapter);
        bind.bookDetailReviewList.setLayoutManager(new LinearLayoutManager(activity));

        bind.back.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();  // Navigate back to the previous fragment
        });

        bind.writeReviewBtn.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.newReviewFragment));

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void setupView(Book book) {
        Picasso.get().load(book.getImageUrl()).into(bind.bookDetailCover);
        bind.bookDetailBookName.setText(book.getTitle());
        bind.bookDetailBookAuthor.setText(book.getAuthorString());
        bind.bookDetailBookDescription.setText(book.getDescription());

    }

    private void generateData() {
//        reviews.add(new Review("id", "What a book!", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Lorem sed risus ultricies tristique nulla aliquet. Eget nunc lobortis mattis aliquam faucibus purus in massa.", "This is user id", "This is book it", new com.google.firebase.Timestamp(1, 1), 5));
    }
}