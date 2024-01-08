package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Lend extends Post implements Serializable, Parcelable {
    private LatLng location;

    public Lend() {
        // empty constructor needed for firebase
    }

    public Lend(LatLng location) {
        this.location = location;
    }

    public Lend(String id, String title, String content, User user, Book book, Long timestamp, LatLng location) {
        super(id, title, content, user, book, timestamp);
        this.location = location;
    }

    protected Lend(Parcel in) {
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Lend> CREATOR = new Creator<Lend>() {
        @Override
        public Lend createFromParcel(Parcel in) {
            return new Lend(in);
        }

        @Override
        public Lend[] newArray(int size) {
            return new Lend[size];
        }
    };

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(location, i);
    }
}
