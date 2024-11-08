package com.example.frolic;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for displaying and managing event details in admin view.
 * Allows administrators to view event details and delete events from the system.
 */
public class AdminEventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "AdminEventDetails";
    private String eventId;
    private FirebaseFirestore db;
    private TextView tvTitle;
    private TextView tvEventDate;
    private TextView tvEventDescription;

    /**
     * Initializes the activity, sets up the UI components and loads event details.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                          this Bundle contains the most recent data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_delete_event_screen);

        Log.d(TAG, "AdminEventDetailsActivity onCreate started");

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");

        Log.d(TAG, "Received eventId: " + eventId);

        if (eventId == null || eventId.isEmpty()) {
            Log.e(TAG, "No event ID provided");
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        loadEventDetails();
    }

    /**
     * Initializes view references and verifies their existence.
     */
    private void initializeViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventDescription = findViewById(R.id.tvEventDescription);

        if (tvTitle == null || tvEventDate == null || tvEventDescription == null) {
            Log.e(TAG, "Failed to initialize one or more views");
            Toast.makeText(this, "Error: Layout initialization failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**
     * Sets up click listeners for the back and delete buttons.
     */
    private void setupClickListeners() {
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDeleteEvent = findViewById(R.id.btnDeleteEvent);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
        }

        if (btnDeleteEvent != null) {
            btnDeleteEvent.setOnClickListener(v -> {
                Log.d(TAG, "Delete button clicked for event: " + eventId);
                showDeleteConfirmation();
            });
        }
    }

    /**
     * Shows a confirmation dialog before deleting an event.
     */
    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Loads event details from Firestore database.
     */
    private void loadEventDetails() {
        Log.d(TAG, "Loading details for event: " + eventId);

        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        Log.d(TAG, "Event loaded successfully: " + event.getEventName());
                        updateUI(event);
                    } else {
                        Log.e(TAG, "Event data is null for ID: " + eventId);
                        Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading event: " + e.getMessage());
                    Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    /**
     * Updates the UI with event details.
     * @param event The event object containing details to display
     */
    private void updateUI(Event event) {
        tvTitle.setText(event.getEventName());
        tvEventDate.setText("Date: " + event.getEventDate().toString());

        String details = String.format("Event Details:\nMax Attendees: %d\nWaitlist Limit: %d\n" +
                        "Geolocation Required: %s\nNotifications: %s",
                event.getMaxConfirmed(),
                event.getWaitlistLimit(),
                event.isGeolocationRequired() ? "Yes" : "No",
                event.isReceiveNotification() ? "Enabled" : "Disabled");

        tvEventDescription.setText(details);
    }

    /**
     * Deletes the current event from Firestore database.
     */
    private void deleteEvent() {
        Log.d(TAG, "Attempting to delete event: " + eventId);

        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Event deleted successfully: " + eventId);
                    Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event: " + e.getMessage());
                    Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                });
    }
}