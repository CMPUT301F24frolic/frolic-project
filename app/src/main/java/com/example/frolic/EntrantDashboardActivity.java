package com.example.frolic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Main dashboard activity for entrants. Provides navigation to key features
 * such as viewing events, managing profile, and scanning QR codes.
 * This is the primary navigation hub for entrant users after login.
 */
public class EntrantDashboardActivity extends AppCompatActivity {
    private static final String TAG = "EntrantDashboard";
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        // Initialize views and set up click listeners
        setupNavigationOptions();

        // Set up action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Entrant Dashboard");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_switch_role) {
            showRoleSwitchDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    /**
     * Shows dialog to confirm role switch
     */
    private void showRoleSwitchDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Switch Role")
                .setMessage("Switch to Organizer role?")
                .setPositiveButton("Switch", (dialog, which) -> switchToOrganizerRole())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles switching user to Organizer role
     */
    private void switchToOrganizerRole() {
        String deviceId = getIntent().getStringExtra("deviceId");

        // First check if user already has an organizer profile
        db.collection("organizers").document(deviceId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Already has organizer profile, just switch to it
                        startActivity(new Intent(this, OrganizerDashboardActivity.class)
                                .putExtra("deviceId", deviceId));
                        finish();
                    } else {
                        // Need to create organizer profile first
                        startActivity(new Intent(this, OrganizerEditProfile.class)
                                .putExtra("deviceId", deviceId)
                                .putExtra("isNewRole", true));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking organizer profile", e);
                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Failed to switch role. Please try again.")
                            .setPositiveButton("OK", null)
                            .show();
                });
    }
}