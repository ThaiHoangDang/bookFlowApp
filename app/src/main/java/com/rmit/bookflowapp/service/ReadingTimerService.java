package com.rmit.bookflowapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.rmit.bookflowapp.R;

public class ReadingTimerService extends Service {
    private static final String TAG = "TimerService";
    private static final long INTERVAL = 1000; // 1 second interval
    private long elapsedTime = 0;
    private boolean isTimerRunning = false;

    private Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedTime += INTERVAL;
            Log.d(TAG, "Elapsed Time: " + elapsedTime / 1000 + " seconds");
            sendBroadcastUpdate(elapsedTime);
            handler.postDelayed(this, INTERVAL);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        if (!isTimerRunning) {
            createNotificationChannel();
            startForeground(1, buildNotification("Timer is running"));

            handler.postDelayed(timerRunnable, INTERVAL);
            isTimerRunning = true;
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            handler.removeCallbacks(timerRunnable);
            stopForeground(true);
            isTimerRunning = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "timer_channel",
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "timer_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Timer Service")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        return builder.build();
    }

    private void sendNotification(String contentText) {
        Notification notification = buildNotification(contentText);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, notification);
    }

    private void sendBroadcastUpdate(long elapsedTime) {
        Intent intent = new Intent("TIMER_UPDATE");
        intent.putExtra("ELAPSED_TIME", elapsedTime);
        sendBroadcast(intent);
    }
}
