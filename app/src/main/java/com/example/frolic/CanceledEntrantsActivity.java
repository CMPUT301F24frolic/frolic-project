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

public class CanceledEntrantsActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvCanceledListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> canceledEntrantIds;
    private Button btnNotifyAllEntrants;
    private FirebaseFirestore db;
    private String eventId;
    private NotificationHelper notificationHelper;

    /**
     * Called when the activity is first created. Sets up the RecyclerView, populates the
     * list of canceled entrants, and configures the UI.
     *
     * @param savedInstanceState the state of the activity as previously saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canceled_entrants_screen);

        Intent intent = getIntent();
        canceledEntrantIds = intent.getStringArrayListExtra("entrantList");

        db = FirebaseFirestore.getInstance();
        eventId = intent.getStringExtra("eventId");
        notificationHelper = new NotificationHelper();

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvCanceledListCount = findViewById(R.id.llCanceled).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        // Set up RecyclerView with EntrantsAdapter
        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(canceledEntrantIds);
        rvEntrants.setAdapter(adapter);

        // Update count
        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        // Display a message if there are no entrants in the canceled list
        if (canceledEntrantIds == null || canceledEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the canceled list.", Toast.LENGTH_SHORT).show();
        }

        // Set up notification sending
        btnNotifyAllEntrants.setOnClickListener(v -> notifyAllCancelled());
    }

    /**
     * Updates the count display with the number of entrants in the canceled list.
     */
    private void updateCountDisplay() {
        if (canceledEntrantIds != null) {
            tvCanceledListCount.setText(String.valueOf(canceledEntrantIds.size()));
        } else {
            tvCanceledListCount.setText("0");
        }
    }

    /**
     * Sends notifications to all entrants in the canceled list for the event.
     */
    public void notifyAllCancelled() {
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
                                ArrayList<String> canceledList = (ArrayList<String>) lotterySnapshot.get("canceledListIds");
                                canceledList = canceledList != null ? canceledList : new ArrayList<>();

                                sendNotifications(canceledList,
                                        "Reminder of your cancellation",
                                        "The organizer of " + eventName + " wants to remind you that you have been canceled for the event.");

                                Toast.makeText(this, "Notifications sent to all canceled.", Toast.LENGTH_SHORT).show();
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