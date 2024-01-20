package com.rmit.bookflowapp.Model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String role; // ADMIN or USER
    private String imageId;
    private boolean verified = false;

    private List<String> favoriteBooks = new ArrayList<>();

    public User() {
        // empty constructor needed for firebase
    }

    public User(String id, String email, String name, String role, String imageId, boolean verified) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.imageId = imageId;
        this.verified = verified;
    }

    public User(String id, String email, String name, String role, String imageId, List<String> favoriteBooks, boolean verified) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.imageId = imageId;
        this.favoriteBooks = favoriteBooks;
        this.verified = verified;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<String> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
