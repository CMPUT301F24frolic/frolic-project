package com.example.frolic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Activity for administrators to manage user profiles.
 * Provides functionality to view and remove user profiles.
 */
public class AdminProfilesActivity extends AppCompatActivity {
    private static final String TAG = "AdminProfilesActivity";
    private FirebaseFirestore db;
    private RecyclerView rvProfiles;
    private AdminProfilesAdapter adapter;
    private ArrayList<Identity> profileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_entrant_view);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupRecyclerView();
        loadProfiles();
    }

    /**
     * Initializes the views and sets up basic UI interactions.
     */
    private void initializeViews() {
        rvProfiles = findViewById(R.id.rvProfiles);
        Button btnBack = findViewById(R.id.tvBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     */
    private void setupRecyclerView() {
        if (rvProfiles != null) {
            rvProfiles.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AdminProfilesAdapter(profileList, new AdminProfilesAdapter.OnProfileActionListener() {
                @Override
                public void onProfileDeleted(String userId, String userType) {
                    showDeleteConfirmation(userId, userType);
                }

                @Override
                public void onProfileSelected(Identity profile) {
                    // The adapter will handle showing the details
                    if (profile == null) {
                        Log.e(TAG, "Attempted to view details of null profile");
                        Toast.makeText(AdminProfilesActivity.this,
                                "Error: Invalid profile data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG, "Profile selected: " + profile.getDeviceID());
                }
            }, this);
            rvProfiles.setAdapter(adapter);
        }
    }

    /**
     * Shows a confirmation dialog before deleting a profile.
     * @param userId The ID of the user to delete
     * @param userType The type of user ("entrant" or "organizer")
     */
    private void showDeleteConfirmation(String userId, String userType) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProfile(userId, userType))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Loads all user profiles from Firestore database.
     */
    private void loadProfiles() {
        profileList.clear();
        loadUserType("entrants");
        loadUserType("organizers");
    }

    /**
     * Loads users of a specific type from Firestore.
     * @param userType The type of users to load ("entrants" or "organizers")
     */
    private void loadUserType(String userType) {
        Log.d(TAG, "Loading profiles for type: " + userType);

        db.collection(userType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    HashSet<String> loadedIds = new HashSet<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String deviceId = document.getId();

                            if (loadedIds.contains(deviceId)) {
                                continue;
                            }
                            loadedIds.add(deviceId);

                            String name = document.getString("name");
                            String email = document.getString("email");
                            String role = userType.endsWith("s") ?
                                    userType.substring(0, userType.length() - 1).toUpperCase() :
                                    userType.toUpperCase();
                            String FCM_token = document.getString("FCM_token");

                            int phoneNumber = document.getLong("phoneNumber") != null ?
                                    document.getLong("phoneNumber").intValue() : 0;

                            Identity profile = new Identity(deviceId, name, email, role, phoneNumber, FCM_token);

                            if (document.contains("admin")) {
                                profile.setAdmin(document.getBoolean("admin"));
                            }
                            if (document.contains("notifications")) {
                                profile.setNotifications(document.getBoolean("notifications"));
                            }

                            Log.d(TAG, String.format("Loaded profile: %s (%s) with role: %s",
                                    profile.getName(), userType, profile.getRole()));

                            profileList.removeIf(p -> p.getDeviceID().equals(deviceId));
                            profileList.add(profile);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading profile from document: " + document.getId(), e);
                        }
                    }
                    adapter.updateProfiles(profileList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading " + userType, e);
                    Toast.makeText(this,
                            "Failed to load " + userType, Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes a user profile from the Firestore database.
     * @param userId The ID of the user to delete
     * @param userType The type of user ("entrant" or "organizer")
     */
    private void deleteProfile(String userId, String userType) {
        String collectionPath = userType;
        if (!collectionPath.endsWith("s")) {
            collectionPath += "s";
        }

        Log.d(TAG, "Attempting to delete profile: " + userId + " from " + collectionPath);

        db.collection(collectionPath)
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully deleted profile: " + userId);
                    Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    loadProfiles();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting profile", e);
                    Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfiles();
    }
}