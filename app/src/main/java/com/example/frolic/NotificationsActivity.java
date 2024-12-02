package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying notifications for a specific device ID.
 */
public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private ArrayList<String> notificationList = new ArrayList<>();
    private String deviceId;
    private Button btnBack;  // Back button declaration

    // Firestore database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notificationsRef = db.collection("notifications");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize the back button and set click listener
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, EntrantDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Get the device ID from the Intent
        deviceId = getIntent().getStringExtra("deviceId");

        // Load notifications from Firestore if the device ID is available
        if (deviceId != null) {
            loadNotifications();
        } else {
            Toast.makeText(this, "Device ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads notifications for the specified device ID from Firestore.
     */
    private void loadNotifications() {
        List<NotificationStorageHelper.NotificationItem> notifications =
                NotificationStorageHelper.getNotifications(this, deviceId);

        notificationList.clear();
        for (NotificationStorageHelper.NotificationItem notif : notifications) {
            notificationList.add(notif.getTitle() + "\n" + notif.getMessage());
        }

        adapter.notifyDataSetChanged();

        if (notificationList.isEmpty()) {
            Toast.makeText(NotificationsActivity.this,
                    "No notifications found.", Toast.LENGTH_SHORT).show();
        }
    }
}


    /**
     * Adapter class for displaying notifications in a RecyclerView.
     */
    class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

        private ArrayList<String> notifications;

        public NotificationsAdapter(ArrayList<String> notifications) {
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            holder.notificationText.setText(notifications.get(position));
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        /**
         * ViewHolder class for individual notification items.
         */
        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView notificationText;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                notificationText = itemView.findViewById(R.id.tvNotificationText);
            }


        }

    }



