package com.example.frolic;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

/**
 * Activity for administrators to manage events in the system.
 * Provides functionality for viewing all events, displaying their details,
 * and managing event lifecycle operations such as deletion. Implements
 * a RecyclerView-based interface with dialog interactions for event management.
 */
public class AdminEventsActivity extends AppCompatActivity {
    private static final String TAG = "AdminEventsActivity";
    private FirebaseFirestore db;
    private RecyclerView rvEvents;
    private AdminEventsAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    /**
     * Initializes the activity, setting up the RecyclerView, adapter, and
     * loading initial event data from Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the data it most
     *                          recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupRecyclerView();
        loadEvents();
    }

    /**
     * Initializes view references and sets up basic UI interactions.
     * Configures the back button for navigation.
     */
    private void initializeViews() {
        rvEvents = findViewById(R.id.rvEvents);
        TextView tvBack = findViewById(R.id.tvBackManageEvents);

        if (tvBack != null) {
            tvBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     * Configures the event action listeners for the adapter.
     */
    private void setupRecyclerView() {
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AdminEventsAdapter(eventList,
                    new AdminEventsAdapter.OnEventActionListener() {
                        @Override
                        public void onEventDeleted(String eventId) {
                            showDeleteConfirmation(eventId);
                        }

                        @Override
                        public void onEventSelected(Event event) {
                            // For future implementation of event selection handling
                        }
                    }, this);
            rvEvents.setAdapter(adapter);
        }
    }

    /**
     * Loads events from Firestore database and updates the RecyclerView.
     * Clears existing events and refreshes with current data.
     */
    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            eventList.add(event);
                        }
                    }
                    adapter.updateEvents(eventList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading events", e);
                    Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Displays a confirmation dialog before deleting an event.
     * Provides the user with a chance to cancel the deletion operation.
     *
     * @param eventId The ID of the event to be deleted
     */
    private void showDeleteConfirmation(String eventId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(eventId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes an event from the Firestore database.
     * Refreshes the event list upon successful deletion.
     *
     * @param eventId The ID of the event to delete
     */
    private void deleteEvent(String eventId) {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    loadEvents();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event", e);
                    Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                });
    }
}