package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String title;
    private Author[] authors;
    private Genre[] genres;
    private String description;
    private String imageId;

    public Book() {
        // empty constructor needed for firebase
    }

    public Book(String title, Author[] authors, Genre[] genres, String description, String imageId) {
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.description = description;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author[] getAuthors() {
        return authors;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    protected Book(Parcel in) {
        title = in.readString();
        authors = in.createTypedArray(Author.CREATOR);
        genres = in.createTypedArray(Genre.CREATOR);
        description = in.readString();
        imageId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedArray(authors, flags);
        dest.writeTypedArray(genres, flags);
        dest.writeString(description);
        dest.writeString(imageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
