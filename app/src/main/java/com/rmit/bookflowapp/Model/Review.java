package com.rmit.bookflowapp.Model;

import java.io.Serializable;
import java.util.List;

public class Review extends Post implements Serializable {

    private int rating;

    public Review() {
        // empty constructor needed for firebase
    }

    public Review(int rating) {
        this.rating = rating;
    }


    public Review(String id, String title, String content, User user, Book book, Long timestamp, List<String> likedUsers, int rating) {
        super(id, title, content, user, book, timestamp, likedUsers);
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
