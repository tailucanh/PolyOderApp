package com.example.polyOder.PushNotification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplicaton extends Application {

    public static final String CHANNEL_ID = "notification_name";

    @Override
    public void onCreate() {
        super.onCreate();
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PushNotification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
