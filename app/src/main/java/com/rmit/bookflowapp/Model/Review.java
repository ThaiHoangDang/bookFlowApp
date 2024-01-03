package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class Review extends Post implements Parcelable {

    private String bookId;
    private int rating;

    public Review() {
        // empty constructor needed for firebase
    }

    public Review(String bookId, int rating) {
        this.bookId = bookId;
        this.rating = rating;
    }

    public Review(String content, String userId, Timestamp timestamp, String bookId, int rating) {
        super(content, userId, timestamp);
        this.bookId = bookId;
        this.rating = rating;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    protected Review(Parcel in) {
        bookId = in.readString();
        rating = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeInt(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
