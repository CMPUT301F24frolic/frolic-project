package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantMyEvents extends AppCompatActivity {

    private static final String TAG = "EntrantMyEvents";
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private Map<String, String> organizerNames = new HashMap<>(); // Cache for organizer names
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference lotteriesRef = db.collection("lotteries");
    private CollectionReference eventsRef = db.collection("events");
    private String entrantId;

    // Replace with actual entrant ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_list);

        Button btnBack = findViewById(R.id.btnBackDashboard);
        btnBack.setOnClickListener(v -> finish());

        // Retrieve device ID passed from the previous activity
        entrantId = getIntent().getStringExtra("deviceId");
        if (entrantId == null || entrantId.isEmpty()) {
            Log.e(TAG, "Device ID not found in Intent. Exiting activity.");
            finish();
            return;
        }


        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMyEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(adapter);

        // Load filtered events
        loadFilteredEvents();
    }

    /**
     * Loads events filtered by the entrant's participation in lotteries.
     */
    private void loadFilteredEvents() {
        if (entrantId == null || entrantId.isEmpty()) {
            Log.w(TAG, "Entrant ID is null or empty. Cannot load events.");
            return;
        }

        lotteriesRef.whereArrayContains("waitingListIds", entrantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> relevantEventIds = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                relevantEventIds.add(document.getString("eventId"));
                            }
                        }
                        loadEventsByEventIds(relevantEventIds); // Load events based on relevant IDs
                    } else {
                        Log.w(TAG, "Error querying lotteries.", task.getException());
                        Toast.makeText(EntrantMyEvents.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Loads events from Firestore based on event IDs.
     *
     * @param eventIds The list of event IDs to load.
     */
    private void loadEventsByEventIds(List<String> eventIds) {
        if (eventIds.isEmpty()) {
            Toast.makeText(this, "No relevant events found.", Toast.LENGTH_SHORT).show();
            return;
        }

        eventsRef.whereIn("eventId", eventIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    fetchOrganizerName(event); // Fetch the organizer's name for each event
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Error loading events.", task.getException());
                        Toast.makeText(EntrantMyEvents.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Fetches an event by its ID and adds it to the list.
     *
     * @param eventId The ID of the event to fetch.
     */
    private void fetchEvent(String eventId) {
        eventsRef.document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            Event event = documentSnapshot.toObject(Event.class);
            if (event != null) {
                fetchOrganizerName(event); // Fetch the organizer's name for the event
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch event: " + eventId, e);
        });
    }

    /**
     * Fetches the organizer's name for an event and updates the adapter.
     *
     * @param event The event for which to fetch the organizer's name.
     */
    private void fetchOrganizerName(Event event) {
        String organizerId = event.getOrganizerId();

        // Check if the organizer's name is already cached
        if (organizerNames.containsKey(organizerId)) {
            String organizerName = organizerNames.get(organizerId);
            eventsList.add(event);
            adapter.notifyDataSetChanged();
        } else {
            // Fetch the organizer's name from Firestore
            DocumentReference organizerRef = db.collection("organizers").document(organizerId);
            organizerRef.get().addOnSuccessListener(documentSnapshot -> {
                String organizerName = documentSnapshot.getString("name");
                if (organizerName != null) {
                    organizerNames.put(organizerId, organizerName); // Cache the organizer name
                    eventsList.add(event);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "Organizer name not found for ID: " + organizerId);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to fetch organizer name", e);
            });
        }
    }

    /**
     * Adapter class for displaying events in a RecyclerView.
     */
    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

        private ArrayList<Event> events;

        public EventsAdapter(ArrayList<Event> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event, parent, false);
            return new EventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
            Event event = events.get(position);
            holder.eventName.setText(event.getEventName());

            // Display the organizer name from the cache, if available
            String organizerName = organizerNames.get(event.getOrganizerId());
            holder.organizerName.setText("Organized by: " + (organizerName != null ? organizerName : "Unknown"));



            // Set click listener to navigate to EventDetailsActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(EntrantMyEvents.this, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getEventId());
                intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
                startActivity(intent);
           });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        public class EventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, organizerName;

            public EventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                organizerName = itemView.findViewById(R.id.tvOrganizerName);
            }
        }
    }
}

