package com.example.frolic;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Main entry point for the application. Handles user initialization,
 * checking device ID against Firebase database, and directing users
 * to appropriate screens based on their registration status.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
    private String deviceId;

    /**
     * Initializes the activity, sets up Firebase connection, and begins the
     * user verification process.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     * Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        checkUserExists();
        Log.d(TAG, "Device ID: " + deviceId);
    }

    /**
     * Queries Firestore to check if the current device ID is associated with an existing user.
     * Redirects to profile creation for new users or main menu for existing users.
     * Implements error handling for database query failures.
     */
    private void checkUserExists() {
        db.collection("users")
                .document(deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            processExistingUser(document);
                        } else {
                            redirectToProfileCreation();
                        }
                    } else {
                        Log.w(TAG, "Error checking user existence", task.getException());
                        // TODO: Implement proper error handling UI
                    }
                });
    }

    /**
     * Creates an Identity object from Firestore document data and redirects to main menu.
     *
     * @param document Firestore document containing user data
     */
    private void processExistingUser(DocumentSnapshot document) {
        Identity user = new Identity(
                deviceId,
                document.getString("name"),
                document.getString("email"),
                document.getLong("phoneNumber") != null ?
                        document.getLong("phoneNumber").intValue() : 0
        );
        Intent intent = new Intent(this, EntrantDashboardActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
        finish();
    }

    /**
     * Redirects user to profile creation screen with device ID.
     * Called when no existing user profile is found for the current device.
     */
    private void redirectToProfileCreation() {
        Intent intent = new Intent(MainActivity.this, EntrantEditProfile.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
        Log.d(TAG, "No user found, redirecting to profile creation");
    }
}