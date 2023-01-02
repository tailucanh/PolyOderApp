package com.example.polyOder.PushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.polyOder.MainActivity;
import com.example.polyOder.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyNotificator extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        Log.d("TAG", "Message data payload: " + remoteMessage.getData());
        String title = data.get("title");
        String content = data.get("body");

        Log.d("TAG", "onMessageReceived: "+title);
        sendNotification(title, content);
    }

    private void sendNotification(String title,String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

//        String channelId = "fcm_default_channel";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, MyApplicaton.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null){
            notificationManager.notify(1, notification);
        }
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("TAG", "onNewToken: "+token);
    }
}
