package com.example.frolic;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationHelper {

    private FirebaseFirestore db;

    public NotificationHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Add a notification to the array in the specified document
    public void addNotification(Context context, String deviceId, String title, String message) {
        db.collection("notifications").document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Add notification to existing array
                        Map<String, String> notification = new HashMap<>();
                        notification.put("Title", title);
                        notification.put("Message", message);

                        db.collection("notifications").document(deviceId)
                                .update("notifs", FieldValue.arrayUnion(notification))
                                .addOnSuccessListener(aVoid -> Log.d("NotificationHelper", "Notification added successfully!"))
                                .addOnFailureListener(e -> Log.e("NotificationHelper", "Error adding notification: " + e.getMessage()));
                    } else {
                        // Create new document with 'notifs' field
                        Map<String, String> notification = new HashMap<>();
                        notification.put("Title", title);
                        notification.put("Message", message);

                        ArrayList<Map<String, String>> notifs = new ArrayList<>();
                        notifs.add(notification);

                        Map<String, Object> data = new HashMap<>();
                        data.put("notifs", notifs);

                        db.collection("notifications").document(deviceId)
                                .set(data)
                                .addOnSuccessListener(aVoid -> Log.d("NotificationHelper", "Document created and notification added successfully!"))
                                .addOnFailureListener(e -> Log.e("NotificationHelper", "Error creating document: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> Log.e("NotificationHelper", "Error retrieving document: " + e.getMessage()));
    }


    // Retrieve all notifications for a specific device ID
    public void getNotifications(Context context, String deviceId) {
        // Check if notifications are enabled for the entrant
        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifications")) {
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notifications");

                        if (Boolean.TRUE.equals(notificationsEnabled)) {
                            // Notifications are enabled, proceed to fetch and display notifications
                            fetchAndDisplayNotifications(context, deviceId);
                        } else {
                            // Notifications are disabled
                            deleteNotifications(deviceId);
                            Log.d("NotificationHelper", "Notifications are disabled for deviceId: " + deviceId);
                        }
                    } else {
                        Log.d("NotificationHelper", "Entrant document does not exist or 'notifications' field is missing.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationHelper", "Error checking notifications setting: " + e.getMessage());
                });
    }

    /**
     * Fetch notifications from Firestore and display them locally.
     */
    private void fetchAndDisplayNotifications(Context context, String deviceId) {
        db.collection("notifications").document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("notifs")) {
                        ArrayList<Map<String, String>> notifs =
                                (ArrayList<Map<String, String>>) documentSnapshot.get("notifs");

                        if (notifs != null && !notifs.isEmpty()) {
                            for (Map<String, String> notif : notifs) {
                                String title = notif.get("Title");
                                String message = notif.get("Message");

                                if (title != null && message != null) {
                                    NotificationStorageHelper.saveNotification(context, deviceId, title, message);

                                    displayNotification(context, title, message);
                                }
                            }

                            // Delete notifications from Firestore after displaying them
                            deleteNotifications(deviceId);
                        } else {
                            Toast.makeText(context, "No notifications found.", Toast.LENGTH_SHORT).show();
                            Log.d("NotificationHelper", "No notifications found.");
                        }
                    } else {
                        Log.d("NotificationHelper", "Notification document does not exist or contains no 'notifs'.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationHelper", "Error retrieving notifications: " + e.getMessage());
                });
    }


    /**
     * Displays a local notification using the Android NotificationManager.
     *
     * @param context the application context
     * @param title   the title of the notification
     * @param message the body of the notification
     */
    private void displayNotification(Context context, String title, String message) {
        String channelId = "frolic_notifications";
        String channelName = "Frolic Notifications";

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create and display the notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your app's notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Automatically removes the notification when tapped
                .build();

        // Generate a unique notification ID
        int notificationId = UUID.randomUUID().hashCode();

        notificationManager.notify(notificationId, notification);
    }


    // Delete only the "notifs" field from the specified document
    public void deleteNotifications(String deviceId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("notifs", FieldValue.delete()); // Indicate that the "notifs" field should be deleted

        db.collection("notifications").document(deviceId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("NotificationHelper", "Notifications deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationHelper", "Error deleting notifications: " + e.getMessage());
                });
    }

}
