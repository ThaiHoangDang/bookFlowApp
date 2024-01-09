package com.rmit.bookflowapp.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.fragment.BookDetailFragment;

import java.util.List;

public class BookDetailViewModel {
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

    private void fetchBookReviews() {

    }
}
