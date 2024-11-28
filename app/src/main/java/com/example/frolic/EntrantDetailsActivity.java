package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * EntrantDetailsActivity displays details for an event's entrants, including counts for
 * different entrant statuses (Waiting List, Chosen, Canceled) and a list of confirmed entrants.
 * Users can filter entrants by status to view specific lists.
 */
public class EntrantDetailsActivity extends AppCompatActivity {

    private TextView tvBack, tvFilter, tvWaitingListCount, tvChosenCount, tvCanceledCount;
    private Button btnPickEntrants, btnNotifyAllEntrants;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> confirmedEntrantIds = new ArrayList<>();
    private ArrayList<String> waitingListIds = new ArrayList<>();
    private ArrayList<String> chosenListIds = new ArrayList<>();
    private ArrayList<String> canceledListIds = new ArrayList<>();
    private FirebaseFirestore db;
    private String eventId;
    private NotificationHelper notificationHelper;

    /**
     * Initializes the activity, setting up Firebase, retrieving event data from the intent, and
     * configuring the UI elements and RecyclerView to display entrant details.
     *
     * @param savedInstanceState the saved instance state for activity recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_details_screen);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");

        tvBack = findViewById(R.id.tvBack);
        tvFilter = findViewById(R.id.tvFilter);
        tvWaitingListCount = findViewById(R.id.tvWaitingListCount);
        tvChosenCount = findViewById(R.id.tvChosenCount);
        tvCanceledCount = findViewById(R.id.tvCanceledCount);
        rvEntrants = findViewById(R.id.rvEntrants);

        btnPickEntrants = findViewById(R.id.btnPickEntrants);
        btnNotifyAllEntrants = findViewById(R.id.btnNotifyAllEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(confirmedEntrantIds); // Pass the list of entrant IDs
        rvEntrants.setAdapter(adapter);

        notificationHelper = new NotificationHelper();

        loadEntrantDetails();

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up filter dropdown
        tvFilter.setOnClickListener(this::showFilterMenu);

        // Set up lottery picking
        btnPickEntrants.setOnClickListener(v -> pickEntrants());

        // Set up notification sending
        btnNotifyAllEntrants.setOnClickListener(v -> notifyAllEntrants());
    }


    /**
     * Picks the entrants from the Firebase document and removes them from the waiting list.
     * Also updates the Firebase LotterySystem document to reflect the changes.
     */
    private void pickEntrants() {
        db.collection("lotteries").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e("PickEntrants", "Lottery document not found for eventId: " + eventId);
                        Toast.makeText(this, "Lottery not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LotterySystem lottery = documentSnapshot.toObject(LotterySystem.class);
                    if (lottery == null) {
                        Log.e("PickEntrants", "LotterySystem object is null");
                        return;
                    }

                    // Capture state before changes
                    ArrayList<String> prevInviteds = new ArrayList<>(lottery.getInvitedListIds());

                    // Perform the lottery draw
                    lottery.drawLottery();

                    // Calculate differences
                    ArrayList<String> newInviteds = new ArrayList<>(lottery.getInvitedListIds());
                    ArrayList<String> diff = new ArrayList<>(newInviteds);
                    diff.removeAll(prevInviteds);

                    // Debugging info
                    Log.d("PickEntrants", "New inviteds: " + newInviteds);
                    Log.d("PickEntrants", "Difference (new picks): " + diff);

                    // Prepare updates
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("invitedListIds", newInviteds);
                    updates.put("waitingListIds", lottery.getWaitingListIds());

                    // Update Firestore
                    db.collection("lotteries").document(eventId)
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("PickEntrants", "Firestore updated successfully");

                                // Fetch event details to build the notification message
                                db.collection("events").document(eventId)
                                        .get()
                                        .addOnSuccessListener(eventSnapshot -> {
                                            if (!eventSnapshot.exists()) {
                                                Log.e("sendNotifications", "Event not found for eventId: " + eventId);
                                                return;
                                            }

                                            String eventTitle = eventSnapshot.getString("eventName");
                                            if (eventTitle == null) {
                                                Log.e("sendNotifications", "Event name is null");
                                                return;
                                            }

                                            // Send notifications to winners
                                            String title = "Invited to " + eventTitle;
                                            String body = "Congrats! You have been picked by the lottery for " + eventTitle + "!";
                                            sendNotifications(diff, title, body);

                                            // Send notifications to losers
                                            title = "Lost lottery for " + eventTitle;
                                            body = "Sorry! The lottery has been pulled and you have not been picked for " + eventTitle + ".";
                                            sendNotifications(waitingListIds, title, body);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("sendNotifications", "Error retrieving event details", e);
                                        });

                                loadEntrantDetails();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("PickEntrants", "Error updating Firestore", e);
                                Toast.makeText(this, "Failed to update lottery!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("PickEntrants", "Error fetching lottery document", e);
                    Toast.makeText(this, "Error loading lottery details", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Sends notifications to a list of recipients with a given title and body.
     *
     * @param recipientIds the list of recipient IDs to notify
     * @param title the title of the notification
     * @param body the body of the notification
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




    /**
     * Loads entrant details from Firestore for the current event, including entrant counts and IDs.
     * Updates the UI with entrant counts and populates the RecyclerView with confirmed entrants.
     */
    private void loadEntrantDetails() {
        db.collection("lotteries").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e("loadEntrantDetails", "Lottery document not found for eventId: " + eventId);
                        return;
                    }

                    // Safely retrieve lists
                    waitingListIds = (ArrayList<String>) documentSnapshot.get("waitingListIds");
                    chosenListIds = (ArrayList<String>) documentSnapshot.get("invitedListIds");
                    canceledListIds = (ArrayList<String>) documentSnapshot.get("canceledListIds");
                    confirmedEntrantIds = (ArrayList<String>) documentSnapshot.get("confirmedListIds");

                    // Avoid nulls
                    waitingListIds = waitingListIds != null ? waitingListIds : new ArrayList<>();
                    chosenListIds = chosenListIds != null ? chosenListIds : new ArrayList<>();
                    canceledListIds = canceledListIds != null ? canceledListIds : new ArrayList<>();
                    confirmedEntrantIds = confirmedEntrantIds != null ? confirmedEntrantIds : new ArrayList<>();

                    // Update UI
                    tvWaitingListCount.setText(String.valueOf(waitingListIds.size()));
                    tvChosenCount.setText(String.valueOf(chosenListIds.size()));
                    tvCanceledCount.setText(String.valueOf(canceledListIds.size()));

                    // Update RecyclerView
                    adapter.updateEntrantsList(confirmedEntrantIds);
                })
                .addOnFailureListener(e -> {
                    Log.e("loadEntrantDetails", "Error loading lottery system data", e);
                    Toast.makeText(this, "Error loading entrant details", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Displays a filter menu allowing the user to filter entrants by status (Waiting List, Chosen, or Canceled).
     *
     * @param view the view that triggers the popup menu display
     */
    private void showFilterMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.entrant_list_filter_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onFilterMenuItemClicked);
        popup.show();
    }

    /**
     * Handles the selection of a filter menu item by starting a new activity for the chosen list type.
     *
     * @param menuItem the selected menu item in the filter menu
     * @return true if the menu item was successfully handled; false otherwise
     */
    private boolean onFilterMenuItemClicked(MenuItem menuItem) {
        Intent intent;
        int itemId = menuItem.getItemId();

        if (itemId == R.id.filter_waiting_list) {
            intent = new Intent(this, WaitingListActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putStringArrayListExtra("entrantList", waitingListIds);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.filter_chosen) {
            intent = new Intent(this, ChosenEntrantsActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putStringArrayListExtra("entrantList", chosenListIds);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.filter_canceled) {
            intent = new Intent(this, CanceledEntrantsActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putStringArrayListExtra("entrantList", canceledListIds);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Sends notifications to all entrants in all lists (invited, waiting, canceled) for the event.
     */
    public void notifyAllEntrants() {
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

                                // Safely retrieve lists
                                ArrayList<String> invitedList = (ArrayList<String>) lotterySnapshot.get("invitedListIds");
                                ArrayList<String> waitingList = (ArrayList<String>) lotterySnapshot.get("waitingListIds");
                                ArrayList<String> canceledList = (ArrayList<String>) lotterySnapshot.get("canceledListIds");

                                // Avoid null lists
                                invitedList = invitedList != null ? invitedList : new ArrayList<>();
                                waitingList = waitingList != null ? waitingList : new ArrayList<>();
                                canceledList = canceledList != null ? canceledList : new ArrayList<>();

                                // Send notifications
                                sendNotifications(invitedList,
                                        "Reminder of your acceptance",
                                        "The organizer of " + eventName + " wants to remind you to accept or decline your invitation!");

                                sendNotifications(waitingList,
                                        "Reminder of your waiting",
                                        "The organizer of " + eventName + " wants to remind you that you are still waiting for the lottery!");

                                sendNotifications(canceledList,
                                        "Reminder of your cancellation",
                                        "The organizer of " + eventName + " wants to remind you that you have been canceled for the event.");

                                Toast.makeText(this, "Notifications sent to all entrants.", Toast.LENGTH_SHORT).show();
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

}
