package com.example.frolic;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class YourFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "YourFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // Log the token to Logcat
        Log.d("FCM_TOKEN", "New FCM Registration Token: " + token);
//        sendTokenToServer(token);
    }
//
//    // Optional: Send the token to your server for notification purposes
//    private void sendTokenToServer(String token) {
//        // Replace this with actual code to send the token to your backend server.
//        Log.d("FCM_TOKEN", "Sending token to server: " + token);
//    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM_DEBUG", "Message received from: " + remoteMessage.getFrom());
        if (remoteMessage.getNotification() != null) {
            Log.d("FCM_DEBUG", "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d("FCM_DEBUG", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }


    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1001, builder.build());
    }

}
