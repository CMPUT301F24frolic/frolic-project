package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

/**
 * Activity for administrators to manage all events in the system.
 * Provides functionality to view and delete events.
 */
public class AdminEventsActivity extends AppCompatActivity {
    private static final String TAG = "AdminEventsActivity";
    private FirebaseFirestore db;
    private RecyclerView rvEvents;
    private AdminEventsAdapter adapter;
    private ArrayList<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        db = FirebaseFirestore.getInstance();
        setupRecyclerView();
        loadEvents();

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvEvents = findViewById(R.id.recyclerViewEvents);
        if (rvEvents != null) {
            rvEvents.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AdminEventsAdapter(eventList, new AdminEventsAdapter.OnEventActionListener() {
                @Override
                public void onEventDeleted(String eventId) {
                    deleteEvent(eventId);
                }

                @Override
                public void onEventSelected(Event event) {
                    Log.d(TAG, "Starting AdminEventDetailsActivity with eventId: " + event.getEventId());
                    Intent intent = new Intent(AdminEventsActivity.this, AdminEventDetailsActivity.class);
                    intent.putExtra("eventId", event.getEventId());
                    startActivity(intent);
                }
            });
            rvEvents.setAdapter(adapter);
        }
    }

    private void loadEvents() {
        db.collection("events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
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

    private void deleteEvent(String eventId) {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    loadEvents(); // Reload the list
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event", e);
                    Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                });
    }
}