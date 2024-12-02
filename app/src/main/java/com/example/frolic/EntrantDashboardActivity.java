package com.example.frolic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
            Intent intent = new Intent(this, EventsListActivity.class);  // Connects to EventsListActivity
            intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
            startActivity(intent);
        });

        myEvents.setOnClickListener(v -> {
            // TODO: Navigate to my events
            // Intent intent = new Intent(this, MyEventsActivity.class);
            // startActivity(intent);
            Intent intent = new Intent(this, EntrantMyEvents.class);
            intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));  // Pass device ID if needed
            startActivity(intent);

        });

        notifications.setOnClickListener(v -> {
            // TODO: Navigate to notifications
            // Intent intent = new Intent(this, NotificationsActivity.class);
            // startActivity(intent);
            Intent intent = new Intent(this, NotificationsActivity.class);
            intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantEditProfile.class);
            intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
            intent.putExtra("fromRoleSelection", false);
            startActivity(intent);
        });

        scanQR.setOnClickListener(v -> {
            // TODO: Implement QR scanning
            // Intent intent = new Intent(this, QRScanActivity.class);
            // startActivity(intent);
            Intent intent = new Intent(this, QRScanActivity.class);
            intent.putExtra("eventData", "Your event data here");
            startActivity(intent);

        });
    }

    /**
     * Shows dialog to confirm role switch
     */
    private void showRoleSwitchDialog() {
        String deviceId = getIntent().getStringExtra("deviceId");

        if (deviceId == null) {
            Log.e(TAG, "Device ID is missing");
            Toast.makeText(this, "Error: Device ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        boolean isAdmin = Boolean.TRUE.equals(documentSnapshot.getBoolean("admin"));

                        // Create dialog with dynamic role options
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Switch Role");

                        String[] roles = isAdmin
                                ? new String[]{"Organizer", "Admin"}  // Include Admin if the user is an admin
                                : new String[]{"Organizer"};         // Only Organizer if not an admin

                        builder.setItems(roles, (dialog, which) -> {
                            switch (which) {
                                case 0: // Organizer
                                    switchToOrganizerRole();
                                    break;
                                case 1: // Admin
                                    switchToAdminRole();
                                    break;
                                default:
                                    break;
                            }
                        });

                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                    } else {
                        Log.e(TAG, "User document not found");
                        Toast.makeText(this, "User not found. Unable to switch roles.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data: " + e.getMessage());
                    Toast.makeText(this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Switches the user to the Admin role and navigates to the Admin Dashboard.
     */
    private void switchToAdminRole() {
        String deviceId = getIntent().getStringExtra("deviceId");

        Intent intent = new Intent(this, AdminDashboardActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
        finish();
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