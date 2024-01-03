package com.example.cleanconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class Site implements Parcelable {

    @Exclude
    private String siteId;
    private String ownerId;
    private String title;
    private String locationDetails;
    private String date;
    private List<String> joinedUserIds; // List of user IDs who joined
    private double amountOfWasteCollected;
    private double latitude;
    private double longitude;
    private String address;
    private String imageUrl;

    // Empty constructor required for Firestore
    public Site() {
    }

    public Site(String siteId, String ownerId, String title, String locationDetails, String date, List<String> joinedUserIds, double amountOfWasteCollected, double latitude, double longitude, String address, String imageUrl) {
        this.siteId = siteId;
        this.ownerId = ownerId;
        this.title = title;
        this.locationDetails = locationDetails;
        this.date = date;
        this.joinedUserIds = joinedUserIds;
        this.amountOfWasteCollected = amountOfWasteCollected;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getTitle() {
        return title;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getDate() {
        return date;
    }

    public List<String> getJoinedUserIds() {
        return joinedUserIds;
    }

    public double getAmountOfWasteCollected() {
        return amountOfWasteCollected;
    }

    // Exclude the siteId field when writing to Firestore
    @Exclude
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "CleanUpSite{" +
                "siteId='" + siteId + '\'' +
                ", locationName='" + title + '\'' +
                ", latlng='" + latitude + "/" + longitude + '\'' +
                ", locationDetails='" + locationDetails + '\'' +
                ", date='" + date + '\'' +
                ", address='" + address + '\'' +
                ", joinedUserIds=" + joinedUserIds +
                ", amountOfWasteCollected=" + amountOfWasteCollected +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(siteId);
        dest.writeString(ownerId);
        dest.writeString(title);
        dest.writeString(locationDetails);
        dest.writeString(date);
        dest.writeStringList(joinedUserIds);
        dest.writeDouble(amountOfWasteCollected);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imageUrl);
        dest.writeString(address);
    }
    public static final Parcelable.Creator<Site> CREATOR = new Parcelable.Creator<Site>() {
        @Override
        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    private Site(Parcel in) {
        siteId = in.readString();
        ownerId = in.readString();
        title = in.readString();
        locationDetails = in.readString();
        date = in.readString();
        joinedUserIds = in.createStringArrayList();
        amountOfWasteCollected = in.readDouble();
        latitude = in.readDouble();
        longitude = in.readDouble();
        imageUrl = in.readString();
        address = in.readString();
    }
}

