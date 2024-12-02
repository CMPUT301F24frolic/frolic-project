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
 * ChosenEntrantsActivity displays a list of entrants who were selected in the lottery system
 * for a specific event. It retrieves the list of chosen entrant IDs from the intent and displays
 * each entrant's details in a RecyclerView. The count of chosen entrants is also dynamically
 * displayed at the top of the screen.
 */
public class ChosenEntrantsActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvChosenListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> chosenEntrantIds;
    private Button btnNotifyAllEntrants;
    private FirebaseFirestore db;
    private String eventId;
    private NotificationHelper notificationHelper;

    /**
     * Initializes the activity, sets up the UI components, retrieves the chosen entrant IDs from
     * the intent, and displays them in a RecyclerView. Displays a message if no entrants are available.
     *
     * @param savedInstanceState the saved instance state for activity recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosen_entrants_screen);

        Intent intent = getIntent();
        chosenEntrantIds = intent.getStringArrayListExtra("entrantList");

        db = FirebaseFirestore.getInstance();
        eventId = intent.getStringExtra("eventId");
        notificationHelper = new NotificationHelper();

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvChosenListCount = findViewById(R.id.llChosenList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(chosenEntrantIds);
        rvEntrants.setAdapter(adapter);

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (chosenEntrantIds == null || chosenEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the chosen list.", Toast.LENGTH_SHORT).show();
        }

        // Set up notification sending
        btnNotifyAllEntrants.setOnClickListener(v -> notifyAllChosen());
    }

    /**
     * Updates the count display of chosen entrants in the UI. Sets the text to "0" if
     * the chosen entrant list is null.
     */
    private void updateCountDisplay() {
        if (chosenEntrantIds != null) {
            tvChosenListCount.setText(String.valueOf(chosenEntrantIds.size()));
        } else {
            tvChosenListCount.setText("0");
        }
    }

    /**
     * Sends notifications to all entrants in the chosen list for the event.
     */
    public void notifyAllChosen() {
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
                                ArrayList<String> invitedList = (ArrayList<String>) lotterySnapshot.get("invitedListIds");
                                invitedList = invitedList != null ? invitedList : new ArrayList<>();

                                sendNotifications(invitedList,
                                        "Reminder of your invitation",
                                        "The organizer of " + eventName + " wants to remind you that you have been invited for the event and is waiting on your decision to accept or decline the invitation.");

                                Toast.makeText(this, "Notifications sent to all invited users.", Toast.LENGTH_SHORT).show();
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