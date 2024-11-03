package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for selecting the user's role in the application.
 * Users can choose between being an Entrant or an Organizer.
 * This choice determines their initial profile type and available features.
 */
public class RoleSelectionActivity extends AppCompatActivity {
    private String deviceId;

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
    }
}