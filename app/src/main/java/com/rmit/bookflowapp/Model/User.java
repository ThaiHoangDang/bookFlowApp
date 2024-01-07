package com.rmit.bookflowapp.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String role; // ADMIN or USER
    private String imageId;

    public User() {
        // empty constructor needed for firebase
    }

    public User(String id, String email, String name, String role, String imageId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.imageId = imageId;
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

    public String getImageUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/striking-water-408603.appspot.com/o/" + getImageId() + "?alt=media";
    }
}
