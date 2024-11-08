package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity providing the main dashboard interface for admin users.
 * Allows access to event management, profile management, and other admin functions.
 */
public class AdminDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AdminDashboardActivity";
    private FirebaseFirestore db;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard_screen);

        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("deviceId");

        if (deviceId == null) {
            Log.e(TAG, "No device ID provided");
            Toast.makeText(this, "Error: Device ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
        verifyAdminStatus();
    }

    /**
     * Sets up click listeners and initializes UI elements.
     */
    private void setupUI() {
        TextView manageEvents = findViewById(R.id.tvManageEvents);
        TextView manageProfiles = findViewById(R.id.tvManageProfiles);
        // Removing back button functionality as requested

        manageEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEventsActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });

        manageProfiles.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProfilesActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
        });
    }

    /**
     * Prevents going back from the admin dashboard
     */
    @Override
    public void onBackPressed() {
        Toast.makeText(this,
                "Please use the menu options to navigate",
                Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    /**
     * Verifies that the current user has admin privileges by checking the admin flag
     * in the entrants collection. Redirects to main activity if verification fails.
     */
    private void verifyAdminStatus() {
        db.collection("entrants")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        if (!Boolean.TRUE.equals(isAdmin)) {
                            Log.w(TAG, "User is not an admin: " + deviceId);
                            Toast.makeText(this, "Unauthorized access", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.w(TAG, "User document not found: " + deviceId);
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error verifying admin status: " + e.getMessage());
                    Toast.makeText(this, "Error verifying admin status", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}