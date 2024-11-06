package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for selecting the user's role in the application.
 * Users can choose between being an Entrant or an Organizer.
 * This choice determines their initial profile type and available features.
 */
public class RoleSelectionActivity extends AppCompatActivity {
    private String deviceId;
    private FirebaseFirestore db;
    private Button btnAdmin;

    /**
     * Initializes the activity and sets up role selection buttons.
     * Receives device ID from previous activity for user identification.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the most recently supplied data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_selection_screen);

        deviceId = getIntent().getStringExtra("deviceId");

        // Hide admin button by default
        btnAdmin.setVisibility(View.GONE);

        checkIfAdmin();

        setupButtons();

    }

    /**
     * Sets up click listeners for the role selection buttons.
     * Each button directs to the appropriate profile creation screen.
     */
    private void setupButtons() {
        Button btnEntrant = findViewById(R.id.btnEntrant);
        Button btnOrganizer = findViewById(R.id.btnOrganizer);

        btnEntrant.setOnClickListener(v -> {
            Intent intent = new Intent(this, EntrantEditProfile.class);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("role", "ENTRANT");
            startActivity(intent);
            finish();
        });

        btnOrganizer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrganizerEditProfile.class);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("role", "ORGANIZER");
            startActivity(intent);
            finish();
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.putExtra("deviceId", deviceId);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Checks if the current user is an admin. If so, displays the admin button.
     */
    private void checkIfAdmin() {
        db.collection("entrants").whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Boolean isAdmin = document.getBoolean("admin");

                        if (Boolean.TRUE.equals(isAdmin)) {
                            btnAdmin.setVisibility(View.VISIBLE);  // Show admin button if user is an admin
                        }
                    } else {
                        Log.d("RoleSelectionActivity", "No user found with this device ID.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RoleSelectionActivity", "Error checking admin status", e);
                    Toast.makeText(this, "Error loading roles.", Toast.LENGTH_SHORT).show();
                });
    }
}
