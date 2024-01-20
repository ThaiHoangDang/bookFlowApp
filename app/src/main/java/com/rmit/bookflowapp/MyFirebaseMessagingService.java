package com.rmit.bookflowapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if the message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Handle data payload
        }

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Handle notification payload
        }
    }

    @Override
    public void onNewToken(String token) {
        // Handle token refresh
        Log.d(TAG, "Refreshed token: " + token);

        // Send the new token to your server if needed
    }
}
