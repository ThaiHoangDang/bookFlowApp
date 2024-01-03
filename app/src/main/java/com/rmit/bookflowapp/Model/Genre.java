package com.rmit.bookflowapp.Model;

import java.io.Serializable;

public class Genre implements Serializable {
    private String id;
    private String name;
    private String description;
    private String imageId;

    public Genre() {
        // empty constructor needed for firebase
    }

    public Genre(String id, String name, String description, String imageId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
