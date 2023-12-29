package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

public class Lend extends Post implements Parcelable {
    private LatLng location;

    public Lend() {
        // empty constructor needed for firebase
    }

    public Lend(LatLng location) {
        this.location = location;
    }

    public Lend(String content, String userId, Timestamp timestamp, LatLng location) {
        super(content, userId, timestamp);
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    protected Lend(Parcel in) {
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
