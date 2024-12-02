package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button btnNotifyAllEntrants;
    private NotificationHelper notificationHelper;
    /**
     * Initializes the activity, retrieves the list of waiting entrants from the intent,
     * sets up the views, and configures the RecyclerView to display the list of waiting entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_list_screen);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        waitingListEntrantIds = intent.getStringArrayListExtra("entrantList");
        canceledListIds = intent.getStringArrayListExtra("canceledList");
        eventId = intent.getStringExtra("eventId");
        notificationHelper = new NotificationHelper();

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvWaitingListCount = findViewById(R.id.llWaitingList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(waitingListEntrantIds, true);
        rvEntrants.setAdapter(adapter);

        adapter.setOnItemClickListener((parent, view, position, id) -> {
            String entrantId = waitingListEntrantIds.get(position);
            showEntrantDialog(entrantId);
        });

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (waitingListEntrantIds == null || waitingListEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the waiting list.", Toast.LENGTH_SHORT).show();
        }

        btnNotifyAllEntrants.setOnClickListener(v -> notifyAllWaiting());
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

        db.collection("lotteries").document(eventId)
                .update("waitingListIds", waitingListEntrantIds)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entrant removed from waitlist.", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    updateCountDisplay();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update waitlist.", Toast.LENGTH_SHORT).show());

        db.collection("lotteries").document(eventId)
                .update("canceledListIds", canceledListIds)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Entrant removed from waitlist.", Toast.LENGTH_SHORT).show();
                    updateCountDisplay();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update waitlist.", Toast.LENGTH_SHORT).show());
    }


    /**
     * Sends notifications to all entrants in the waiting list for the event.
     */
    public void notifyAllWaiting() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(eventSnapshot -> {
                    if (!eventSnapshot.exists()) {
                        Log.e("notifyAllEntrants", "Event document not found for eventId: " + eventId);
                        Toast.makeText(this, "Event not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String eventName = eventSnapshot.getString("eventName");
                    if (eventName == null) {
                        Log.e("notifyAllEntrants", "Event name is null for eventId: " + eventId);
                        return;
                    }

                    db.collection("lotteries").document(eventId)
                            .get()
                            .addOnSuccessListener(lotterySnapshot -> {
                                if (!lotterySnapshot.exists()) {
                                    Log.e("notifyAllEntrants", "Lottery document not found for eventId: " + eventId);
                                    Toast.makeText(this, "Lottery details not found!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<String> waitingList = (ArrayList<String>) lotterySnapshot.get("waitingListIds");
                                waitingList = waitingList != null ? waitingList : new ArrayList<>();

                                sendNotifications(waitingList,
                                        "Reminder of your invitation",
                                        "The organizer of " + eventName + " wants to remind you that you are still waiting for an invitation.");

                                Toast.makeText(this, "Notifications sent to all waiting users.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("notifyAllEntrants", "Error retrieving lottery details: " + e.getMessage());
                                Toast.makeText(this, "Failed to load lottery details.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("notifyAllEntrants", "Error retrieving event details: " + e.getMessage());
                    Toast.makeText(this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sends notifications to a list of recipients with a given title and body.
     *
     * @param recipientIds the list of recipient IDs to notify
     * @param title        the title of the notification
     * @param body         the body of the notification
     */
    private void sendNotifications(ArrayList<String> recipientIds, String title, String body) {
        for (String recipientId : recipientIds) {
            notificationHelper.addNotification(
                    this,
                    recipientId,
                    title,
                    body
            );
        }
        Log.d("sendNotifications", "Notifications sent to: " + recipientIds);
    }
}