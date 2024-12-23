package com.example.frolic;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Main dashboard activity for organizers. Provides navigation to key features
 * such as creating events, managing facilities, and viewing entrants.
 * This is the primary navigation hub for organizer users after login.
 */
public class OrganizerDashboardActivity extends AppCompatActivity {
    private static final String TAG = "OrganizerDashboard";
    private FirebaseFirestore db;
    private String deviceId;
    private String facilityId;
    private NotificationHelper notificationHelper;

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
        setContentView(R.layout.organizer_dashboard_screen);

        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("deviceId");

        // Grab notifications
        notificationHelper = new NotificationHelper();
        notificationHelper.getNotifications(this, getIntent().getStringExtra("deviceId"));

        // Fetch facilityId based on the organizer ID
        db.collection("organizers")
                .document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("facilityId")) {
                        facilityId = document.getString("facilityId");
                        Log.d(TAG, "Facility ID retrieved from organizer: " + facilityId);
                    } else {
                        Log.d(TAG, "Facility ID not found in organizer document");
                        Toast.makeText(this, "Facility ID not found for this organizer", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching facility ID", e);
                    Toast.makeText(this, "Error accessing facility information", Toast.LENGTH_SHORT).show();
                });

        // Initialize views and set up click listeners
        setupNavigationOptions();

        // Set up action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Organizer Dashboard");
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
        TextView createEvents = findViewById(R.id.tvCreateEvents);
        TextView manageEvents = findViewById(R.id.tvManageEvents);
        TextView myProfile = findViewById(R.id.tvMyProfile);


        createEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListEventActivity.class);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("facilityId", facilityId);
            startActivity(intent);
        });

        manageEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageEventsActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });

        myProfile.setOnClickListener(v -> {
            // Check if facility exists and load for editing
            db.collection("facilities")
                    .whereEqualTo("organizerId", deviceId)
                    .get()
                    .addOnSuccessListener(queryDocuments -> {
                        Intent intent = new Intent(this, OrganizerEditProfile.class);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("fromRoleSelection", false);
                        if (!queryDocuments.isEmpty()) {
                            // Facility exists, pass its ID for editing
                            String facilityId = queryDocuments.getDocuments().get(0).getId();
                            intent.putExtra("facilityId", facilityId);
                            Log.d(TAG, "Existing facility found: " + facilityId);
                        } else {
                            Log.d(TAG, "No existing facility found");
                        }
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking facility", e);
                        showErrorDialog("Error accessing facility information");
                    });
        });

    }

    /**
     * Shows dialog to confirm role switch
     */
    private void showRoleSwitchDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Switch Role")
                .setMessage("Switch to Entrant role?")
                .setPositiveButton("Switch", (dialog, which) -> switchToEntrantRole())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles switching user to Entrant role
     */
    private void switchToEntrantRole() {
        String deviceId = getIntent().getStringExtra("deviceId");

        // First check if user already has an entrant profile
        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Already has entrant profile, just switch to it
                        startActivity(new Intent(this, EntrantDashboardActivity.class)
                                .putExtra("deviceId", deviceId));
                        finish();
                    } else {
                        // Need to create entrant profile first
                        startActivity(new Intent(this, EntrantEditProfile.class)
                                .putExtra("deviceId", deviceId)
                                .putExtra("isNewRole", true));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking entrant profile", e);
                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Failed to switch role. Please try again.")
                            .setPositiveButton("OK", null)
                            .show();
                });
    }

    private void proceedWithRoleSwitch() {
        db.collection("Entrants and Organizers")
                .document(deviceId)
                .update("role", "ENTRANT")
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(this, EntrantDashboardActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error switching role", e);
                    showErrorDialog("Failed to switch role. Please try again.");
                });
    }
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}