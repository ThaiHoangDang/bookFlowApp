package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private String title;
    private List<Author> authors;
    private List<Genre> genres;
    private String description;
    private String imageId;

    public Book() {
        // empty constructor needed for firebase
    }

    public Book(String title, List<Author> authors, List<Genre> genres, String description, String imageId) {
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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
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
}
