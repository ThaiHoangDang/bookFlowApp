package com.rmit.bookflowapp.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Lend extends Post implements Serializable {
    private LatLng location;

    public Lend() {
        // empty constructor needed for firebase
    }

    public Lend(LatLng location) {
        this.location = location;
    }

    public Lend(String id, String title, String content, String userId, String bookId, Timestamp timestamp, LatLng location) {
        super(id, title, content, userId, bookId, timestamp);
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
