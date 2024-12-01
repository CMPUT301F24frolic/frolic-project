package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Activity to display the waiting list of entrants for an event.
 * Shows the list of entrants who are on the waiting list, with a count
 * of total entrants. Allows navigation back to the previous screen.
 */
public class WaitingListActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private TextView tvBack, tvTitle, tvWaitingListCount;
    private RecyclerView rvEntrants;
    private String eventId;
    private EntrantsAdapter adapter;
    private ArrayList<String> waitingListEntrantIds, canceledListIds;

    /**
     * Initializes the activity, retrieves the list of waiting entrants from the intent,
     * sets up the views, and configures the RecyclerView to display the list of waiting entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_list_screen);

        Intent intent = getIntent();
        waitingListEntrantIds = intent.getStringArrayListExtra("entrantList");
        canceledListIds = intent.getStringArrayListExtra("canceledList");
        eventId = intent.getStringExtra("eventId");

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvWaitingListCount = findViewById(R.id.llWaitingList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(waitingListEntrantIds, true); // Enable clickability
        rvEntrants.setAdapter(adapter);

        adapter.setOnItemClickListener((parent, view, position, id) -> {
            String entrantId = waitingListEntrantIds.get(position);
            showEntrantDialog(entrantId); // Show dialog when item is clicked
        });

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (waitingListEntrantIds == null || waitingListEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the waiting list.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the display of the waiting list count based on the size of waitingListEntrantIds.
     * Sets the count to 0 if the list is null or empty.
     */
    private void updateCountDisplay() {
        if (waitingListEntrantIds != null) {
            tvWaitingListCount.setText(String.valueOf(waitingListEntrantIds.size()));
        } else {
            tvWaitingListCount.setText("0");
        }
    }
    /**
     * Show a dialog with entrant details and options.
     *
     * @param entrantId The ID of the entrant clicked
     */
    private void showEntrantDialog(String entrantId) {
        db.collection("entrants").document(entrantId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Entrant Details");
                builder.setMessage("Name: " + name + "\nEmail: " + email);
                builder.setPositiveButton("Cancel from Waitlist", (dialog, which) -> showCancellationConfirmation(entrantId));
                builder.setNegativeButton("Go Back", (dialog, which) -> dialog.dismiss());
                builder.show();
            } else {
                Toast.makeText(this, "Entrant details not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error loading entrant details.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Show a confirmation dialog for cancellation.
     *
     * @param entrantId The ID of the entrant to cancel
     */
    private void showCancellationConfirmation(String entrantId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Cancellation");
        builder.setMessage("Are you sure you want to remove this entrant from the waitlist?");
        builder.setPositiveButton("Yes", (dialog, which) -> cancelEntrantFromWaitlist(entrantId));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Cancel an entrant from the waitlist, update Firestore, and refresh UI.
     *
     * @param entrantId The ID of the entrant to cancel
     */
    private void cancelEntrantFromWaitlist(String entrantId) {
        waitingListEntrantIds.remove(entrantId);
        canceledListIds.add(entrantId);

        db.collection("lotteries").document(eventId) // Replace "yourEventId" with the actual ID
                .update("waitingListIds", waitingListEntrantIds)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entrant removed from waitlist.", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    updateCountDisplay();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update waitlist.", Toast.LENGTH_SHORT).show());

        db.collection("lotteries").document(eventId) // Replace "yourEventId" with the actual ID
                .update("canceledListIds", canceledListIds)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entrant removed from waitlist.", Toast.LENGTH_SHORT).show();
                    updateCountDisplay();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update waitlist.", Toast.LENGTH_SHORT).show());
    }


}
