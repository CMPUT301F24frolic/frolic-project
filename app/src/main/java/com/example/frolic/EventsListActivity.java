package com.example.frolic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(adapter);

        // Load events from Firestore
        loadEventsFromFirestore();
    }

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
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
                Toast.makeText(EventsListActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adapter class for displaying events in a RecyclerView
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
            holder.eventDescription.setText(event.getEventDesc());
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        public class EventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, eventDescription;

            public EventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                eventDescription = itemView.findViewById(R.id.tvEventDescription);
            }
        }
    }
}

