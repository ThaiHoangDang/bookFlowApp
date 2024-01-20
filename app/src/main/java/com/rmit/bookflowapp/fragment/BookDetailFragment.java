package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ReviewAdapter;
import com.rmit.bookflowapp.databinding.FragmentBookDetailBinding;
import com.rmit.bookflowapp.repository.UserRepository;
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
    private boolean isFavorite = false;
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
        activity.setBottomNavigationBarVisibility(false);

        setupView(book);

        bind.back.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });


        String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserRepository.getInstance().getUserById(firebaseUserId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User currentUser = task.getResult();
                isFavorite = currentUser.getFavoriteBooks().contains(book.getId());
                updateLikeButtonState();

                bind.like.setOnClickListener(v -> {
                    toggleFavoriteStatus();
                    updateLikeButtonState();
                });
            }
        });

        return bind.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh ViewModel data
        viewModel.refreshBookReviews();
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

        bind.startReadingTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BOOK_OBJECT", book);
                Navigation.findNavController(getView()).navigate(R.id.readingTimerFragment, bundle);
            }
        });

        viewModel.getBookReviews().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                reviewAdapter.setItems(reviews);

                updateRating(reviews);
                updateNewReviewBtn(reviews);
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateRating(List<Review> reviews) {

        // return early if there are no reviews
        if (reviews.size() == 0) return;

        int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, sum = 0;

        for (Review review : reviews) {
            sum += review.getRating();

            switch (review.getRating()) {
                case 1:
                    num1 += 1;
                    break;
                case 2:
                    num2 += 1;
                    break;
                case 3:
                    num3 += 1;
                    break;
                case 4:
                    num4 += 1;
                    break;
                case 5:
                    num5 += 1;
                    break;
            }
        }

        // set ratings
        bind.bookDetailReviewCount.setText(Integer.toString(reviews.size()));

        bind.bookDetailTotalScore.setText(String.format("%.1f", (float) sum / reviews.size()) + "/5.0");
        bind.bookDetailRating.setRating((float) sum / reviews.size());
        bind.bookDetailReviewScore.setText(String.format("%.1f", (float) sum / reviews.size()));

        bind.progressBar1.setProgress((int) Math.round(num1 * 100.0 / reviews.size()));
        bind.oneStarPercen.setText(Math.round(num1 * 100.0 / reviews.size()) + "%");

        bind.progressBar2.setProgress((int) Math.round(num2 * 100.0 / reviews.size()));
        bind.twoStarPercen.setText(Math.round(num2 * 100.0 / reviews.size()) + "%");

        bind.progressBar3.setProgress((int) Math.round(num3 * 100.0 / reviews.size()));
        bind.threeStarPercen.setText(Math.round(num3 * 100.0 / reviews.size()) + "%");

        bind.progressBar4.setProgress((int) Math.round(num4 * 100.0 / reviews.size()));
        bind.fourStarPercen.setText(Math.round(num4 * 100.0 / reviews.size()) + "%");

        bind.progressBar5.setProgress((int) Math.round(num5 * 100.0 / reviews.size()));
        bind.fiveStarPercen.setText(Math.round(num5 * 100.0 / reviews.size()) + "%");
        bind.bookDetail5StarCount.setText(Integer.toString(num5));
    }

    @SuppressLint("SetTextI18n")
    private void updateNewReviewBtn(List<Review> reviews) {
        Review myReview = reviews.stream().filter(review -> Objects.equals(review.getUser().getId(), FirebaseAuth.getInstance().getUid())).findFirst().orElse(null);

        // update review
        if (myReview != null) {
            bind.writeReviewBtn.setText("Edit Review");
            bind.writeReviewBtn.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BOOK_OBJECT", book);
                bundle.putSerializable("MY_REVIEW", myReview);
                Navigation.findNavController(getView()).navigate(R.id.newReviewFragment, bundle);
            });

            // create new review
        } else {
            bind.writeReviewBtn.setText("Write A Review");
            bind.writeReviewBtn.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("BOOK_OBJECT", book);
                Navigation.findNavController(getView()).navigate(R.id.newReviewFragment, bundle);
            });
        }
    }

    private void toggleFavoriteStatus() {
        isFavorite = !isFavorite;

        String firebaseUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (isFavorite) {
            UserRepository.getInstance().addToFavorites(firebaseUserId, book.getId());
            Toast.makeText(activity, "Book added to Favorite list!", Toast.LENGTH_SHORT).show();
        } else {
            UserRepository.getInstance().removeFromFavorites(firebaseUserId, book.getId());
            Toast.makeText(activity, "Book removed from Favorite list!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLikeButtonState() {
        if (isFavorite) {
            bind.like.setImageResource(R.drawable.red_favorite_24);
        } else {
            bind.like.setImageResource(R.drawable.baseline_favorite_24);
        }
    }
}