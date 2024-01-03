package com.example.cleanconnect.model;
import com.google.firebase.firestore.Exclude;

public class User {

    private String uid;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String userType;
    // Empty constructor required for Firestore
    public User() {
    }

    public User(String uid, String displayName, String email, String phoneNumber, String profileImageUrl, String userType) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Exclude the userId field when writing to Firestore
    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    // You can include additional setter methods as needed

    @Override
    public String toString() {
        return "User{" +
                "userId='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", type='" + userType + '\'' +
                '}';
    }
}
