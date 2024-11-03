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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity {

    private static final String TAG = "MyEventsActivity";
    private RecyclerView recyclerView;
    private MyEventsAdapter adapter;
    private ArrayList<Event> eventsList = new ArrayList<>();
    private Button btnBack;  // Back button declaration

    // Firestore database instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = db.collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        // Initialize the back button and set click listener
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, EntrantDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMyEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter(eventsList);
        recyclerView.setAdapter(adapter);

        // Load events from Firestore
        loadEventsFromFirestore();
    }

    private void loadEventsFromFirestore() {
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    Toast.makeText(MyEventsActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                    return;
                }

                eventsList.clear();
                for (DocumentSnapshot document : querySnapshot) {
                    Event event = document.toObject(Event.class);
                    eventsList.add(event);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Inner Adapter Class for My Events
     */
    private class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.MyEventsViewHolder> {

        private ArrayList<Event> eventsList;

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
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        /**
         * ViewHolder class for individual event items
         */
        public class MyEventsViewHolder extends RecyclerView.ViewHolder {
            TextView eventName, eventDescription;

            public MyEventsViewHolder(@NonNull View itemView) {
                super(itemView);
                eventName = itemView.findViewById(R.id.tvEventName);
                eventDescription = itemView.findViewById(R.id.tvEventDescription);
            }
        }
    }
}



