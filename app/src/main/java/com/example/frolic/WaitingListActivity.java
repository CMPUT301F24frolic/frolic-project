package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView tvBack, tvTitle, tvWaitingListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> waitingListEntrantIds;
    private Button btnNotifyAllEntrants;
    private FirebaseFirestore db;
    private String eventId;
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

        Intent intent = getIntent();
        waitingListEntrantIds = intent.getStringArrayListExtra("entrantList");

        db = FirebaseFirestore.getInstance();
        eventId = intent.getStringExtra("eventId");
        notificationHelper = new NotificationHelper();

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvWaitingListCount = findViewById(R.id.llWaitingList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(waitingListEntrantIds);
        rvEntrants.setAdapter(adapter);

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (waitingListEntrantIds == null || waitingListEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the waiting list.", Toast.LENGTH_SHORT).show();
        }

        // Set up notification sending
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