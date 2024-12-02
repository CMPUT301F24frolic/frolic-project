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
 * ChosenEntrantsActivity displays a list of entrants who were selected in the lottery system
 * for a specific event. It retrieves the list of chosen entrant IDs from the intent and displays
 * each entrant's details in a RecyclerView. The count of chosen entrants is also dynamically
 * displayed at the top of the screen.
 */
public class ChosenEntrantsActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvChosenListCount;
    private RecyclerView rvEntrants;
    private FirebaseFirestore db;
    private EntrantsAdapter adapter;
    private String eventId;
    private ArrayList<String> chosenEntrantIds, canceledListIds;
    private Button btnNotifyAllEntrants;
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
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        chosenEntrantIds = intent.getStringArrayListExtra("entrantList");
        canceledListIds = intent.getStringArrayListExtra("canceledList");
        eventId = intent.getStringExtra("eventId");
        notificationHelper = new NotificationHelper();

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvChosenListCount = findViewById(R.id.llChosenList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(chosenEntrantIds, true);
        rvEntrants.setAdapter(adapter);
        adapter.setOnItemClickListener((parent, view, position, id) -> {
            String entrantId = chosenEntrantIds.get(position);
            showEntrantDialog(entrantId);
        });

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (chosenEntrantIds == null || chosenEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the chosen list.", Toast.LENGTH_SHORT).show();
        }

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
                builder.setPositiveButton("Cancel from Invited List", (dialog, which) -> showCancellationConfirmation(entrantId));
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
        builder.setMessage("Are you sure you want to remove this entrant from the Invited List?");
        builder.setPositiveButton("Yes", (dialog, which) -> cancelEntrantFromChosenList(entrantId));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Cancel an entrant from the chosen List, update Firestore, and refresh UI.
     *
     * @param entrantId The ID of the entrant to cancel
     */
    private void cancelEntrantFromChosenList(String entrantId) {
        chosenEntrantIds.remove(entrantId);
        canceledListIds.add(entrantId);

        db.collection("lotteries").document(eventId)
                .update("invitedListIds", chosenEntrantIds)
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