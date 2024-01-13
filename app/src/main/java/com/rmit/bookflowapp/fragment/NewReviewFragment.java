package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentNewReviewBinding;
import com.rmit.bookflowapp.repository.PostRepository;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewReviewFragment extends Fragment {

    private static final String TAG = "NewReviewFragment";
    private FragmentNewReviewBinding bind;
    private MainActivity activity;
    private Book book;
    private Review review = null;

    public NewReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        book = (Book) Objects.requireNonNull(arguments).getSerializable("BOOK_OBJECT");

        review = (Review) Objects.requireNonNull(arguments).getSerializable("MY_REVIEW");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentNewReviewBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        // setup display of reviewed book
        View bookToReview = LayoutInflater.from(requireContext()).inflate(R.layout.search_book_card, null);
        ((TextView) bookToReview.findViewById(R.id.searchTitleName)).setText(book.getTitle());
        ((TextView) bookToReview.findViewById(R.id.searchAuthorName)).setText(book.getAuthorString());
        Picasso.get().load(book.getImageUrl()).into((ImageView) bookToReview.findViewById(R.id.searchBookCover));
        bind.newReviewBookHolder.addView(bookToReview);

        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // update form if user has written review before
        if (review != null) {
            bind.reviewTitleText.setText(review.getTitle());
            bind.reviewContentText.setText(review.getContent());
            bind.reviewRating.setRating(review.getRating());
        }

        bind.reviewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewTitle = bind.reviewTitleText.getText().toString();
                String reviewContent = bind.reviewContentText.getText().toString();
                int reviewRating = (int) bind.reviewRating.getRating();

                // check for missing input
                if (reviewTitle.length() == 0 || reviewContent.length() == 0) {
                    Toast.makeText(activity, "Missing input field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // data holder
                Map<String, Object> reviewData = new HashMap<>();
                reviewData.put("bookId", book.getId());
                reviewData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                reviewData.put("title", reviewTitle);
                reviewData.put("content", reviewContent);
                reviewData.put("rating", reviewRating);
                reviewData.put("timestamp", System.currentTimeMillis() / 1000L);

                // push review to db
                if (review != null) {
                    PostRepository.getInstance().updateReview(review.getId(), reviewData).addOnSuccessListener(unused -> {
                        Toast.makeText(activity, "New review added!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }).addOnFailureListener(e -> Toast.makeText(activity, "Upload review failed!", Toast.LENGTH_SHORT).show());
                } else {
                    PostRepository.getInstance().addReview(reviewData).addOnSuccessListener(unused -> {
                        Toast.makeText(activity, "New review added!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }).addOnFailureListener(e -> Toast.makeText(activity, "Upload review failed!", Toast.LENGTH_SHORT).show());
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
}