package com.rmit.bookflowapp.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class Lend extends Post implements Serializable {
    private LatLng location;

    public Lend() {
        // empty constructor needed for firebase
    }

    public Lend(LatLng location) {
        this.location = location;
    }


    public Lend(String id, String title, String content, User user, Book book, Long timestamp, List<String> likedUsers, LatLng location) {
        super(id, title, content, user, book, timestamp, likedUsers);
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
