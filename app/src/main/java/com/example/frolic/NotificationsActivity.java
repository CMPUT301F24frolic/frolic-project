package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Get the device ID from the Intent
        deviceId = getIntent().getStringExtra("deviceId");

        // Load notifications from Firestore
        if (deviceId != null) {
            loadNotifications(deviceId);
        } else {
            Toast.makeText(this, "Device ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadNotifications(String deviceId) {
        notificationsRef.document(deviceId).collection("messages").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notificationList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            if (message != null) {
                                notificationList.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting notifications.", task.getException());
                        Toast.makeText(NotificationsActivity.this, "Failed to load notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Inner Adapter Class for Notifications
     */
    private class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

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
         * ViewHolder class for individual notification items
         */
        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            TextView notificationText;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                notificationText = itemView.findViewById(R.id.tvNotificationText);
            }
        }
    }
}


