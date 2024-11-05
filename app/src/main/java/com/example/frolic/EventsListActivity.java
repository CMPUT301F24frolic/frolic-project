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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

/**
 * Activity for displaying a list of events from the Firestore "events" collection.
 * Users can click on an event to view its details.
 */
public class EventsListActivity extends AppCompatActivity {

    private static final String TAG = "EventsListActivity";
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private ArrayList<Event> eventsList = new ArrayList<>();

    // Firestore database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        // Set up back button to navigate to the previous screen
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish()); // Navigate back to the previous activity

        // Set up RecyclerView for displaying event data
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(adapter);

        // Load events from Firestore and populate the RecyclerView
        loadEventsFromFirestore();
    }

    /**
     * Loads events from the Firestore "events" collection and updates the RecyclerView.
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
                            eventsList.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh the adapter with new data
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
                Toast.makeText(EventsListActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adapter class for displaying events in a RecyclerView.
     */
    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

        private ArrayList<Event> events;

        /**
         * Constructor for the EventsAdapter.
         *
         * @param events The list of events to display.
         */
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
            holder.eventDescription.setText(event.getEventDesc());

            // Set click listener for each item to navigate to EventDetailsActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), EventDetailsActivity.class);
                intent.putExtra("eventId", event.getEventId()); // Pass the event ID or data
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        /**
         * ViewHolder class for individual event items in the RecyclerView.
         */
        public class EventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, eventDescription;

            /**
             * Constructor for the EventsViewHolder.
             *
             * @param itemView The view layout for an event item.
             */
            public EventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                eventDescription = itemView.findViewById(R.id.tvEventDescription);
            }
        }
    }
}


