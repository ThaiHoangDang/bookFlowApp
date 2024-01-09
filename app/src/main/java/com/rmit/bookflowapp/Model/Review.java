package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Review extends Post implements Serializable, Parcelable {

    private int rating;

    public Review() {
        // empty constructor needed for firebase
    }

    public Review(int rating) {
        this.rating = rating;
    }

    public Review(String id, String title, String content, User user, Book book, Long timestamp, int rating) {
        super(id, title, content, user, book, timestamp);
        this.rating = rating;
    }

    protected Review(Parcel in) {
        rating = in.readInt();
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(rating);
    }
}
