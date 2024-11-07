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
    }

    private void pickEntrants() {
        db.collection("lotteries").document(eventId) // Use "lotteries" here
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        LotterySystem lottery = documentSnapshot.toObject(LotterySystem.class);

                        if (lottery == null) {
                            Log.e("PickEntrants", "LotterySystem is null");
                            return;
                        }

                        // Call the lottery draw method to update the lists
                        lottery.drawLottery();

                        ArrayList<String> inviteds = lottery.getInvitedListIds();

                        for(int i = 0; i < inviteds.size(); i++) {
                            Log.d("", "" + inviteds.get(i));
                        }
                        // Update Firestore with the modified lists
                        db.collection("lotteries").document(eventId) // Ensure "lotteries" here too
                                .set(lottery)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("PickEntrants", "LotterySystem updated successfully");
                                    loadEntrantDetails();
                                })
                                .addOnFailureListener(e -> Log.e("PickEntrants", "Error updating LotterySystem", e));

                        Log.d("PickEntrants", "Max attendees: " + lottery.getMaxAttendees());
                    } else {
                        Log.d("EventRead", "No such event found");
                    }
                })
                .addOnFailureListener(e -> Log.e("EventRead", "Error fetching event", e));
    }



    /**
     * Loads entrant details from Firestore for the current event, including entrant counts and IDs.
     * Updates the UI with entrant counts and populates the RecyclerView with confirmed entrants.
     */
    private void loadEntrantDetails() {
        db.collection("lotteries").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve each list from Firestore document
                        waitingListIds = (ArrayList<String>) documentSnapshot.get("waitingListIds");
                        chosenListIds = (ArrayList<String>) documentSnapshot.get("invitedListIds");
                        canceledListIds = (ArrayList<String>) documentSnapshot.get("canceledListIds");
                        confirmedEntrantIds = (ArrayList<String>) documentSnapshot.get("confirmedListIds");

                        // Update counts on the UI
                        tvWaitingListCount.setText(String.valueOf(waitingListIds.size()));
                        tvChosenCount.setText(String.valueOf(chosenListIds.size()));
                        tvCanceledCount.setText(String.valueOf(canceledListIds.size()));

                        // Load confirmed entrants into the RecyclerView
                        adapter.updateEntrantsList(confirmedEntrantIds);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EntrantDetailsActivity", "Error loading lottery system data", e);
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
}
