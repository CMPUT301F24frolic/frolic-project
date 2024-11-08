package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for handling user role selection in the application.
 * Provides options for users to choose between Entrant and Organizer roles.
 * Also handles admin access for authorized users.
 * The selected role determines the user's profile type and available features.
 */
public class RoleSelectionActivity extends AppCompatActivity {
    private static final String TAG = "RoleSelectionActivity";
    private String deviceId;
    private FirebaseFirestore db;
    private Button btnEntrant, btnOrganizer, btnAdmin;
    private TextView tvTitle, tvDescription;

    /**
     * Initializes the activity, sets up the Firebase instance, and configures
     * the role selection interface. Handles the initial visibility of admin features
     * based on user authorization.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                          this Bundle contains the most recently supplied data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_selection_screen);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get device ID from intent
        deviceId = getIntent().getStringExtra("deviceId");

        if (deviceId == null) {
            Log.e(TAG, "No device ID provided");
            Toast.makeText(this, "Error: Device ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();
        initializeButtons();
        checkIfAdmin();
    }

    /**
     * Initializes the views used in the activity.
     */
    private void initializeViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);

        // Set text (although these are already set in XML)
        tvTitle.setText("Choose Your Role");
        tvDescription.setText("Select how you want to use the app");
    }

    /**
     * Initializes and sets up the role selection buttons.
     * Configures click listeners for each role option and handles navigation
     * to the appropriate profile creation or dashboard screens.
     */
    private void initializeButtons() {
        btnEntrant = findViewById(R.id.btnEntrant);
        btnOrganizer = findViewById(R.id.btnOrganizer);
        btnAdmin = findViewById(R.id.btnAdmin);

        // Hide admin button by default until admin status is verified
        btnAdmin.setVisibility(View.GONE);

        // Set up Entrant button
        btnEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantEditProfile.class);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("role", "ENTRANT");
            startActivity(intent);
            finish();
        });

        // Set up Organizer button
        btnOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerEditProfile.class);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("role", "ORGANIZER");
            startActivity(intent);
            finish();
        });

        // Set up Admin button
        btnAdmin.setOnClickListener(v -> {
            if (btnAdmin.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Verifies if the current user has admin privileges by checking the admin flag
     * in the entrants collection. If the user is an admin, the admin option becomes visible.
     */
    private void checkIfAdmin() {
        db.collection("entrants")
                .document(deviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isAdmin = documentSnapshot.getBoolean("admin");
                        if (Boolean.TRUE.equals(isAdmin)) {
                            Log.d(TAG, "Admin status verified for device: " + deviceId);
                            btnAdmin.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "User is not an admin: " + deviceId);
                            btnAdmin.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "User document not found in entrants collection");
                        btnAdmin.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking admin status: " + e.getMessage());
                    btnAdmin.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Error verifying administrative access",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Handles activity cleanup when the activity is destroyed.
     * Ensures proper resource management and cleanup of any ongoing operations.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Add any necessary cleanup here
    }
}