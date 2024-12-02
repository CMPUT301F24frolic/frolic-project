package com.example.frolic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationStorageHelper {
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String NOTIFICATIONS_KEY = "saved_notifications";

    /**
     * Saves a notification to be viewed in the notification activity
     *
     * @param context   the application context
     * @param deviceId  the unique identifier of the device
     * @param title     the title of the notification
     * @param message   the message of the notification
     */

    public static void saveNotification(Context context, String deviceId, String title, String message) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            List<NotificationItem> notifications = getNotifications(context, deviceId);
            notifications.add(new NotificationItem(title, message, System.currentTimeMillis()));

            JSONArray jsonArray = new JSONArray();
            for (NotificationItem notification : notifications) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", notification.getTitle());
                jsonObject.put("message", notification.getMessage());
                jsonObject.put("timestamp", notification.getTimestamp());
                jsonArray.put(jsonObject);
            }

            String key = NOTIFICATIONS_KEY + "_" + deviceId;
            preferences.edit().putString(key, jsonArray.toString()).apply();
        } catch (JSONException e) {
            Log.e("NotificationStorageHelper", "Error saving notification: " + e.getMessage());
        }
    }

    /**
     * Retrieves the list of saved notifications using the deviceId
     *
     * @param context   the application context
     * @param deviceId  the unique identifier of the device
     * @return          the list of saved notifications
     */

    public static List<NotificationItem> getNotifications(Context context, String deviceId) {
        List<NotificationItem> notifications = new ArrayList<>();
        try {
            SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String key = NOTIFICATIONS_KEY + "_" + deviceId;
            String json = preferences.getString(key, "[]");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String message = jsonObject.getString("message");
                long timestamp = jsonObject.getLong("timestamp");
                notifications.add(new NotificationItem(title, message, timestamp));
            }
        } catch (JSONException e) {
            Log.e("NotificationStorageHelper", "Error loading notifications: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Represents notification item.
     */

    public static class NotificationItem {
        private String title;
        private String message;
        private long timestamp;

        public NotificationItem(String title, String message, long timestamp) {
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}