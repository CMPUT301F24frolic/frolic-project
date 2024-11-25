package com.example.frolic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;

/**
 * Entry point of the application.
 * Handles user initialization and directs to appropriate screens based on user state.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
    private String deviceId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "Device ID: " + deviceId);

        setupProgressDialog();
        checkExistingUser();
        FirebaseApp.initializeApp(this);

        
    }

    /**
     * Sets up the progress dialog for loading states.
     */
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    /**
     * Checks if user exists in Firestore and directs to appropriate screen.
     * Shows loading indicator during check and handles potential errors.
     */
    private void checkExistingUser() {
        progressDialog.show();

        // Instead of checking collections, just go to role selection
        progressDialog.dismiss();
        Intent intent = new Intent(this, RoleSelectionActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
        finish();

    /* Original code commented out for reference
    db.collection("entrants").document(deviceId).get()
            .addOnSuccessListener(entrantDoc -> {
                db.collection("organizers").document(deviceId).get()
                        .addOnSuccessListener(organizerDoc -> {
                            db.collection("admins").document(deviceId).get()
                                    .addOnSuccessListener(adminDoc -> {
                                        progressDialog.dismiss();
                                        if (adminDoc.exists()) {
                                            startActivity(new Intent(this, AdminDashboardActivity.class)
                                                    .putExtra("deviceId", deviceId));
                                            finish();
                                        } else if (organizerDoc.exists()) {
                                            startActivity(new Intent(this, OrganizerDashboardActivity.class)
                                                    .putExtra("deviceId", deviceId));
                                            finish();
                                        } else if (entrantDoc.exists()) {
                                            startActivity(new Intent(this, EntrantDashboardActivity.class)
                                                    .putExtra("deviceId", deviceId));
                                            finish();
                                        } else {
                                            startActivity(new Intent(this, RoleSelectionActivity.class)
                                                    .putExtra("deviceId", deviceId));
                                            finish();
                                        }
                                    });
                        });
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.e(TAG, "Error checking user", e);
                showErrorDialog();
            });
    */
    }

    /**
     * Saves the FCM token to Firestore under the user's document.
     * @param userId The user's ID
     * @param token The FCM token
     */
    public void saveTokenToFirestore(String userId, String token) {
        if (userId == null || token == null) {
            Log.e(TAG, "Invalid userId or token!");
            return;
        }

        db.collection("entrants")  // Assuming the user is an entrant, adjust this as needed
                .document(userId)
                .set(Collections.singletonMap("fcmToken", token), SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token updated successfully for user: " + userId))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating FCM token for user: " + userId, e));
    }

    private void handleError(Exception e) {
        progressDialog.dismiss();
        Log.e(TAG, "Error checking user", e);
        showErrorDialog();
    }


    /**
     * Shows error dialog with retry option when database access fails.
     */
    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Connection Error")
                .setMessage("Unable to connect to server. Please check your connection and try again.")
                .setPositiveButton("Retry", (dialog, which) -> checkExistingUser())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /**
     * Navigates to appropriate dashboard based on user's role.
     * Handles different role types and invalid roles.
     *
     * @param document Firestore document containing user data
     */
    private void navigateBasedOnRole(DocumentSnapshot document) {
        String role = document.getString("role");
        Intent intent;

        if (role != null) {
            switch (role) {
                case "ORGANIZER":
                    intent = new Intent(this, OrganizerDashboardActivity.class);
                    break;
                case "ENTRANT":
                    intent = new Intent(this, EntrantDashboardActivity.class);
                    break;
                case "BOTH":
                    intent = new Intent(this, EntrantDashboardActivity.class);
                    break;
                default:
                    Log.w(TAG, "Invalid role found: " + role);
                    // Invalid role - go to role selection
                    intent = new Intent(this, RoleSelectionActivity.class);
                    break;
            }

            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
            finish();
        } else {
            Log.e(TAG, "No role found in document");
            // Remove the duplicate Intent declaration
            startActivity(new Intent(this, RoleSelectionActivity.class)
                    .putExtra("deviceId", deviceId));
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}