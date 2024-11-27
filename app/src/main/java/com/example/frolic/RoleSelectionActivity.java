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
        // Set up Entrant button
        btnEntrant.setOnClickListener(v -> {
            db.collection("entrants")
                    .document(deviceId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
                                // Redirect to Entrant Dashboard if name and email are set
                                Log.d(TAG, "Entrant has completed profile. Redirecting to Dashboard.");
                                Intent intent = new Intent(this, EntrantDashboardActivity.class);
                                intent.putExtra("deviceId", deviceId);
                                startActivity(intent);
                            } else {
                                // Redirect to Edit Profile if profile is incomplete
                                Log.d(TAG, "Entrant profile incomplete. Redirecting to Edit Profile.");
                                Intent intent = new Intent(this, EntrantEditProfile.class);
                                intent.putExtra("deviceId", deviceId);
                                intent.putExtra("role", "ENTRANT");
                                intent.putExtra("fromRoleSelection", true);
                                startActivity(intent);
                            }
                        } else {
                            // Document does not exist, redirect to Edit Profile
                            Log.d(TAG, "Entrant document not found. Redirecting to Edit Profile.");
                            Intent intent = new Intent(this, EntrantEditProfile.class);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("role", "ENTRANT");
                            intent.putExtra("fromRoleSelection", true);
                            startActivity(intent);
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching entrant details: " + e.getMessage());
                        Toast.makeText(this, "Error checking profile details", Toast.LENGTH_SHORT).show();
                    });
        });

        btnOrganizer.setOnClickListener(v -> {
            db.collection("organizers")
                    .document(deviceId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Document snapshot exists for deviceId: " + deviceId);
                            //get name, email and facilityID
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String facilityId = documentSnapshot.getString("facilityId");

                            if (name != null && !name.isEmpty() &&
                                    email != null && !email.isEmpty() &&
                                    facilityId != null && !facilityId.isEmpty()) {
                                // Check the facility document for name
                                db.collection("facilities")
                                        .document(facilityId)
                                        .get()
                                        .addOnSuccessListener(facilitySnapshot -> {
                                            if (facilitySnapshot.exists()) {
                                                String facilityName = facilitySnapshot.getString("name");
                                                //Name exists then redirect to organizer dashboard

                                                if (facilityName != null && !facilityName.isEmpty()) {
                                                    Log.d(TAG, "Facility name exists. Redirecting to Dashboard.");
                                                    Intent intent = new Intent(this, OrganizerDashboardActivity.class);
                                                    intent.putExtra("deviceId", deviceId);
                                                    startActivity(intent);
                                                    finish(); // End the current activity
                                                } else {
                                                    //name does not exist then redirect to edit profile
                                                    Log.d(TAG, "Facility name is missing. Redirecting to Edit Profile.");
                                                    Intent intent = new Intent(this, OrganizerEditProfile.class);
                                                    intent.putExtra("deviceId", deviceId);
                                                    intent.putExtra("role", "ORGANIZER");
                                                    intent.putExtra("fromRoleSelection", true);
                                                    startActivity(intent);
                                                    finish(); // End the current activity
                                                }
                                            } else {
                                                Log.d(TAG, "Facility document not found for facilityId: " + facilityId);
                                                Intent intent = new Intent(this, OrganizerEditProfile.class);
                                                intent.putExtra("deviceId", deviceId);
                                                intent.putExtra("role", "ORGANIZER");
                                                intent.putExtra("fromRoleSelection", true);
                                                startActivity(intent);
                                                finish(); // End the current activity
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error fetching facility details: " + e.getMessage());
                                            Toast.makeText(this, "Error checking facility details", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Log.d(TAG, "Organizer profile incomplete. Redirecting to Edit Profile.");
                                Intent intent = new Intent(this, OrganizerEditProfile.class);
                                intent.putExtra("deviceId", deviceId);
                                intent.putExtra("role", "ORGANIZER");
                                intent.putExtra("fromRoleSelection", true);
                                startActivity(intent);
                                finish(); // End the current activity
                            }
                        } else {
                            Log.d(TAG, "Organizer document not found for deviceId: " + deviceId);
                            Intent intent = new Intent(this, OrganizerEditProfile.class);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("role", "ORGANIZER");
                            intent.putExtra("fromRoleSelection", true);
                            startActivity(intent);
                            finish(); // End the current activity
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching organizer details: " + e.getMessage());
                        Toast.makeText(this, "Error checking profile details", Toast.LENGTH_SHORT).show();
                    });
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