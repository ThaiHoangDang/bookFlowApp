package com.rmit.bookflowapp.Model;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String title;
    private Author[] authors;
    private Genre[] genres;
    private String description;
    private String imageId;

    public Book() {
        // empty constructor needed for firebase
    }

    public Book(String id, String title, Author[] authors, Genre[] genres, String description, String imageId) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.genres = genres;
        this.description = description;
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
