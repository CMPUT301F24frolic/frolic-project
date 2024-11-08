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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that displays the list of events.
 */
public class EventsListActivity extends AppCompatActivity {

    private static final String TAG = "EventsListActivity";
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private Map<String, String> organizerNames = new HashMap<>(); // Cache for organizer names
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(adapter);

        // Load events from Firestore
        loadEventsFromFirestore();
    }

    /**
     * Loads the list of events from Firestore and updates the RecyclerView.
     */
    private void loadEventsFromFirestore() {
        eventsRef.get().addOnCompleteListener(task -> {
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
                Log.w(TAG, "Error getting documents.", task.getException());
                Toast.makeText(EventsListActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
            }
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
                    eventsList.add(event);
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to fetch organizer name", e);
                eventsList.add(event);
                adapter.notifyDataSetChanged();
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
                Intent intent = new Intent(EventsListActivity.this, EventDetailsActivity.class);
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
                organizerName = itemView.findViewById(R.id.tvOrganizerName); // Ensure TextView exists in XML
            }
        }
    }
}

