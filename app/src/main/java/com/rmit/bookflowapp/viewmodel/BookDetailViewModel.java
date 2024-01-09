package com.rmit.bookflowapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class BookDetailViewModel extends ViewModel {
    private final String TAG = "BookDetailViewModel";
    private final MutableLiveData<List<Review>> bookReviews = new MutableLiveData<>();
    private final String bookId;

    public BookDetailViewModel(String bookId) {
        this.bookId = bookId;
        fetchBookReviews();
    }

    public MutableLiveData<List<Review>> getBookReviews() {
        return bookReviews;
    }

    public void refreshBookReviews() {
        fetchBookReviews();
    }

    private void fetchBookReviews() {
        PostRepository.getInstance().getReviewObjectsOfBook(bookId)
                .addOnSuccessListener(allBookReviews -> {
                    List<Review> filteredReviews = new ArrayList<>();

                    for (Review review : allBookReviews) {
                        if (review.getRating() >= 1 && review.getRating() <= 5) {
                            filteredReviews.add(review);
                        }
                    }

                    bookReviews.setValue(filteredReviews);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch book reviews", e);
                });
    }
}
