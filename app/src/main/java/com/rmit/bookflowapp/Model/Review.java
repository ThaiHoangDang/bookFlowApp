package com.rmit.bookflowapp.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Review extends Post implements Serializable {

    private int rating;

    public Review() {
        // empty constructor needed for firebase
    }

    public Review(int rating) {
        this.rating = rating;
    }

    public Review(String id, String title, String content, String userId, String bookId, Timestamp timestamp, int rating) {
        super(id, title, content, userId, bookId, timestamp);
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
