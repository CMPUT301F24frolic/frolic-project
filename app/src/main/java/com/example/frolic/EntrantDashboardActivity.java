package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main dashboard activity for entrants. Provides navigation to key features
 * such as viewing events, managing profile, and scanning QR codes.
 * This is the primary navigation hub for entrant users after login.
 */
public class EntrantDashboardActivity extends AppCompatActivity {
    private static final String TAG = "EntrantDashboard";

    /**
     * Initializes the dashboard activity, setting up click listeners for
     * navigation options and displaying user information.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_dashboard_screen);

        // Initialize views and set up click listeners
        setupNavigationOptions();
    }

    /**
     * Sets up click listeners for all navigation options in the dashboard.
     * Each option redirects to its respective activity or function.
     */
    private void setupNavigationOptions() {
        TextView viewEvents = findViewById(R.id.tvViewEvents);
        TextView myEvents = findViewById(R.id.tvMyEvents);
        TextView notifications = findViewById(R.id.tvNotifications);
        TextView profile = findViewById(R.id.tvProfile);
        Button scanQR = findViewById(R.id.btnScanQR);

        // Set up click listeners
        viewEvents.setOnClickListener(v -> {
            // TODO: Navigate to events list
            // Intent intent = new Intent(this, ViewEventsActivity.class);
            // startActivity(intent);
        });

        myEvents.setOnClickListener(v -> {
            // TODO: Navigate to my events
            // Intent intent = new Intent(this, MyEventsActivity.class);
            // startActivity(intent);
        });

        notifications.setOnClickListener(v -> {
            // TODO: Navigate to notifications
            // Intent intent = new Intent(this, NotificationsActivity.class);
            // startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantEditProfile.class);
            intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
            startActivity(intent);
        });

        scanQR.setOnClickListener(v -> {
            // TODO: Implement QR scanning
            // Intent intent = new Intent(this, QRScanActivity.class);
            // startActivity(intent);
        });
    }
}