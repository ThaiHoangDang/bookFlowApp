package com.example.cleanconnect;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Messaging", remoteMessage.getData().toString());
        // Handle FCM message here
        // You can extract data from remoteMessage.getData() and update your UI
        Toast.makeText(getApplicationContext(), remoteMessage.getData().get("body"), Toast.LENGTH_SHORT).show();
    }
}
