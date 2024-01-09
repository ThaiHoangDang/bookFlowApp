package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ReviewAdapter;
import com.rmit.bookflowapp.databinding.FragmentBookDetailBinding;
import com.rmit.bookflowapp.viewmodel.BookDetailViewModel;
import com.rmit.bookflowapp.viewmodel.factory.BookDetailViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookDetailFragment extends Fragment {
    private static final String TAG = "BookDetailFragment";
    private FragmentBookDetailBinding bind;
    private MainActivity activity;
    private ReviewAdapter reviewAdapter;
    private BookDetailViewModel viewModel;
    private Book book;

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

        assert book != null;
        BookDetailViewModelFactory factory = new BookDetailViewModelFactory(book.getId());
        viewModel = new ViewModelProvider(this, factory).get(BookDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentBookDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        setupView(book);

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
        Picasso.get().load(book.getImageUrl()).into(bind.bookDetailBackgroundImage);
        bind.bookDetailBookName.setText(book.getTitle());
        bind.bookDetailBookAuthor.setText(book.getAuthorString());
        bind.bookDetailBookDescription.setText(book.getDescription());

        // set genres
        for (String genre : book.getGenre()) {
            View genreTag = LayoutInflater.from(requireContext()).inflate(R.layout.genre_tag, null);
            ((TextView) genreTag.findViewById(R.id.genreTagText)).setText(genre);
            bind.bookDetailGenreList.addView(genreTag);
        }

        reviewAdapter = new ReviewAdapter(activity, new ArrayList<>());
        bind.bookDetailReviewList.setAdapter(reviewAdapter);
        bind.bookDetailReviewList.setLayoutManager(new LinearLayoutManager(activity));
        viewModel.getBookReviews().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                reviewAdapter.setItems(reviews);

                updateRating(reviews);
            }
        });
    }

    private void updateRating(List<Review> reviews) {

    }
}