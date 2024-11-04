package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for managing and displaying a list of events organized by the user
 * in a RecyclerView with details fetched from Firebase Firestore.
 * Allows for options to view entrant details, update the event, view event QR code,
 * view map where users join from if geolocation option for event was enabled
 */
public class ManageEventsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView rvEvents;
    private ManageEventsAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private String organizerId;

    /**
     * Initializes the activity, sets up the RecyclerView for displaying events,
     * and loads events organized by the specified organizer.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        db = FirebaseFirestore.getInstance();

        organizerId = getIntent().getStringExtra("deviceId");

        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ManageEventsAdapter(this, eventList);
        rvEvents.setAdapter(adapter);  // Attach the adapter to the RecyclerView

        loadEvents();

        TextView tvBackManageEvents = findViewById(R.id.tvBackManageEvents);
        tvBackManageEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to OrganizerDashboardActivity
                Intent intent = new Intent(ManageEventsActivity.this, OrganizerDashboardActivity.class);
                intent.putExtra("deviceId", organizerId);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Loads events for the specified organizer from Firestore and updates the RecyclerView.
     * Clears the existing event list and fetches current data from Firestore.
     */
    private void loadEvents() {
        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event event = doc.toObject(Event.class);
                        eventList.add(event);
                    }
                    adapter = new ManageEventsAdapter(this, eventList);
                    rvEvents.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }
}

