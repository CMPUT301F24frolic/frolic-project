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
 * Activity that displays the list of events an entrant is enrolled in.
 */
public class MyEventsActivity extends AppCompatActivity {

    private static final String TAG = "MyEventsActivity";
    private RecyclerView recyclerView;
    private MyEventsAdapter adapter;
    private ArrayList<EventWithOrganizer> myEventsList = new ArrayList<>();
    private Button btnBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");
    private String entrantId;

    private static class EventWithOrganizer {
        Event event;
        String organizerName;

        public EventWithOrganizer(Event event, String organizerName) {
            this.event = event;
            this.organizerName = organizerName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        entrantId = getIntent().getStringExtra("entrantId");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewMyEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter(myEventsList);
        recyclerView.setAdapter(adapter);

        loadMyEventsFromFirestore();
    }

    private void loadMyEventsFromFirestore() {
        eventsRef.whereArrayContains("entrantIds", entrantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myEventsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Map<String, String> organizerNamesCache = new HashMap<>();
                            for (DocumentSnapshot document : querySnapshot) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    fetchOrganizerName(event, organizerNamesCache);
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(MyEventsActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchOrganizerName(Event event, Map<String, String> organizerNamesCache) {
        String organizerId = event.getOrganizerId();
        if (organizerNamesCache.containsKey(organizerId)) {
            myEventsList.add(new EventWithOrganizer(event, organizerNamesCache.get(organizerId)));
            adapter.notifyDataSetChanged();
        } else {
            DocumentReference organizerRef = db.collection("organizers").document(organizerId);
            organizerRef.get().addOnSuccessListener(documentSnapshot -> {
                String organizerName = documentSnapshot.getString("name");
                if (organizerName != null) {
                    organizerNamesCache.put(organizerId, organizerName);
                    myEventsList.add(new EventWithOrganizer(event, organizerName));
                } else {
                    myEventsList.add(new EventWithOrganizer(event, "Unknown"));
                }
                adapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                myEventsList.add(new EventWithOrganizer(event, "Unknown"));
                adapter.notifyDataSetChanged();
            });
        }
    }

    private class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder> {

        private ArrayList<EventWithOrganizer> eventsList;

        public MyEventsAdapter(ArrayList<EventWithOrganizer> eventsList) {
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
            EventWithOrganizer eventWithOrganizer = eventsList.get(position);
            holder.eventName.setText(eventWithOrganizer.event.getEventName());
            holder.organizerName.setText("Organized by: " + eventWithOrganizer.organizerName);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MyEventsActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", eventWithOrganizer.event.getEventId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        public class MyEventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, organizerName;

            public MyEventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                organizerName = itemView.findViewById(R.id.tvOrganizerName);
            }
        }
    }
}



