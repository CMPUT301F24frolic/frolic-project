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
 * Activity that displays the list of events an entrant is enrolled in.
 * Users can view detailed information about each event by clicking on it.
 */
public class MyEventsActivity extends AppCompatActivity {

    private static final String TAG = "MyEventsActivity";
    private RecyclerView recyclerView;
    private MyEventsAdapter adapter;
    private ArrayList<Event> myEventsList = new ArrayList<>();
    private Button btnBack;

    // Firestore database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");
    private String entrantId;

    /**
     * Initializes the activity and sets up the UI components.
     * Loads events the entrant is enrolled in from Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Get the entrant ID from the Intent
        entrantId = getIntent().getStringExtra("entrantId");

        // Initialize the back button and set click listener
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Finish current activity to go back
        });

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMyEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter(myEventsList);
        recyclerView.setAdapter(adapter);

        // Load events from Firestore
        loadMyEventsFromFirestore();
    }

    /**
     * Loads the list of events the entrant is enrolled in from Firestore
     * and updates the RecyclerView.
     */
    private void loadMyEventsFromFirestore() {
        eventsRef.whereArrayContains("confirmedEntrants", entrantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myEventsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    myEventsList.add(event);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(MyEventsActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Adapter class for displaying the entrant's events in a RecyclerView.
     */
    private class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder> {

        private ArrayList<Event> eventsList;

        /**
         * Constructor for MyEventsAdapter.
         *
         * @param eventsList The list of events to display.
         */
        public MyEventsAdapter(ArrayList<Event> eventsList) {
            this.eventsList = eventsList;
        }

        @NonNull
        @Override
        public MyEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_events, parent, false);
            return new MyEventsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyEventsViewHolder holder, int position) {
            Event event = eventsList.get(position);
            holder.eventName.setText(event.getEventName());
            holder.eventDescription.setText(event.getEventDesc());

            // Set click listener to navigate to EventDetailsActivity
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MyEventsActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getEventId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        /**
         * ViewHolder class for individual event items.
         */
        public class MyEventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, eventDescription;

            /**
             * Constructor for MyEventsViewHolder.
             *
             * @param itemView The item view layout.
             */
            public MyEventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                eventDescription = itemView.findViewById(R.id.tvEventDescription);
            }
        }
    }
}




