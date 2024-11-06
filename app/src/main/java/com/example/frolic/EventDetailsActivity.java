package com.example.frolic;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for displaying detailed information about a specific event.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private TextView tvEventName, tvEventDate, tvOrganizer;
    private Button btnBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize views
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvOrganizer = findViewById(R.id.tvOrganizer);
        btnBack = findViewById(R.id.btnBack);

        // Set up the back button to finish the activity and go back
        btnBack.setOnClickListener(v -> finish());

        // Get eventId from Intent extras
        eventId = getIntent().getStringExtra("eventId");

        // Load event details from Firestore
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads event details from Firestore based on the provided eventId.
     *
     * @param eventId The ID of the event to load.
     */
    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    tvEventName.setText(event.getEventName() != null ? event.getEventName() : "No name available");
                    tvEventDate.setText(event.getEventDate() != null ? event.getEventDate().toString() : "No date available");
                    fetchOrganizerName(event.getOrganizerId());
                }
            } else {
                Log.w(TAG, "Event document not found.");
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading event details", e));
    }

    /**
     * Fetches the organizer's name using the organizerId from the Identity collection.
     *
     * @param organizerId The ID of the organizer.
     */
    private void fetchOrganizerName(String organizerId) {
        if (organizerId != null) {
            DocumentReference identityRef = db.collection("organizers").document(organizerId);
            identityRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String organizerName = documentSnapshot.getString("name");
                    tvOrganizer.setText("Organized by: " + (organizerName != null ? organizerName : "Unknown"));
                } else {
                    tvOrganizer.setText("Organized by: Unknown");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error loading organizer name", e));
        } else {
            tvOrganizer.setText("Organized by: Unknown");
        }
    }
}


