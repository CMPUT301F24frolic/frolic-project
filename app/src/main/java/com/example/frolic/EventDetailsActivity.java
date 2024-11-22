package com.example.frolic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for displaying detailed information about a specific event.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private TextView tvEventName, tvEventDate, tvOrganizer, tvStatus;
    private Button btnWait, btnLeave, btnJoin, btnDecline, btnBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize views
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvOrganizer = findViewById(R.id.tvOrganizer);
        tvStatus = findViewById(R.id.tvStatus);
        btnWait = findViewById(R.id.btnWait);
        btnLeave = findViewById(R.id.btnLeave);
        btnJoin = findViewById(R.id.btnJoin);
        btnDecline = findViewById(R.id.btnDecline);
        btnBack = findViewById(R.id.btnBack);

        // Set up the back button to finish the activity and go back
        btnBack.setOnClickListener(v -> finish());

        // Get eventId from Intent extras
        eventId = getIntent().getStringExtra("eventId");

        // Retrieve event details from Intent
        String eventName = getIntent().getStringExtra("eventName");
        String organizerId = getIntent().getStringExtra("organizerId");

        // Display event details
        tvEventName.setText(eventName != null ? eventName : "No event name available");

        // Load event details from Firestore
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            Toast.makeText(this, "Event ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads event details from Firestore based on the provided eventId.
     *
     * @param eventId The ID of the event to load.
     */
    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    tvEventName.setText(event.getEventName() != null ? event.getEventName() : "No name available");
                    tvEventDate.setText(event.getEventDate() != null ? event.getEventDate().toString() : "No date available");
                    fetchOrganizerName(event.getOrganizerId());

                    btnWait.setVisibility(View.GONE);
                    btnLeave.setVisibility(View.GONE);
                    btnJoin.setVisibility(View.GONE);
                    btnDecline.setVisibility(View.GONE);
                    DocumentReference lotteryRef = db.collection("lotteries").document(eventId);
                    lotteryRef.get().addOnSuccessListener(documentSnapshot2 -> {
                        if (documentSnapshot2.exists()) {
                            LotterySystem lottery = documentSnapshot2.toObject(LotterySystem.class);
                            String deviceId = getIntent().getStringExtra("deviceId");
                            if (lottery.getWaitingListIds().contains(deviceId)) {
                                // User is already in the waiting list
                                tvStatus.setText("Status: Waiting");
                                btnLeave.setVisibility(View.VISIBLE);
                                btnLeave.setOnClickListener(v -> {
                                    lottery.removeFromWaitingList(deviceId);
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("waitingListIds", lottery.getWaitingListIds());
                                    db.collection("lotteries").document(eventId)
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("LeaveWaitingList", "waitingList updated successfully");
                                                loadEventDetails(eventId);
                                            })
                                            .addOnFailureListener(e -> Log.e("LeaveWaitingList", "Error updating waitingList", e));
                                });
                            }
                            else if (lottery.getInvitedListIds().contains(deviceId)) {
                                // User got invited
                                tvStatus.setText("Status: Invited");
                                btnJoin.setVisibility(View.VISIBLE);
                                btnDecline.setVisibility(View.VISIBLE);

                                btnJoin.setOnClickListener(v -> {
                                    lottery.removeFromInvitedList(deviceId);
                                    Map<String, Object> lotteryUpdates = new HashMap<>();
                                    lotteryUpdates.put("invitedListIds", lottery.getInvitedListIds());
                                    event.addEntrantId(deviceId);
                                    Map<String, Object> eventUpdates = new HashMap<>();
                                    eventUpdates.put("entrantIds", event.getEntrantIds());

                                    db.collection("lotteries").document(eventId)
                                            .update(lotteryUpdates)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("joinEntrantsList", "invitedList updated successfully");
                                                loadEventDetails(eventId);
                                            })
                                            .addOnFailureListener(e -> Log.e("joinEntrantsList", "Error updating invitedList", e));

                                    db.collection("events").document(eventId)
                                            .update(eventUpdates)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("joinEntrantsList", "entrantIds updated successfully");
                                                loadEventDetails(eventId);
                                            })
                                            .addOnFailureListener(e -> Log.e("joinEntrantsList", "Error updating entrantIds", e));
                                });

                                btnDecline.setOnClickListener(v -> {
                                    lottery.removeFromInvitedList(deviceId);
                                    lottery.addToCanceledList(deviceId);
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("invitedListIds", lottery.getInvitedListIds());
                                    updates.put("canceledListIds", lottery.getCanceledListIds());

                                    db.collection("lotteries").document(eventId)
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("declineInvitation", "invitedListIds and canceledListIds updated successfully");
                                                loadEventDetails(eventId);
                                            })
                                            .addOnFailureListener(e -> Log.e("declineInvitation", "Error updating invitedListIds and canceledListIds", e));

                                });
                            }
                            else if (event.getEntrantIds().contains(deviceId)) {
                                // User is already an entrant
                                tvStatus.setText("Status: Entrant");
                            }
                            else if (lottery.getCanceledListIds().contains(deviceId)) {
                                // User canceled invitation
                                tvStatus.setText("Status: Canceled");
                            }
                            else {
                                // User is not involved in the event yet
                                tvStatus.setText("Status: None");
                                btnWait.setVisibility(View.VISIBLE);

                                btnWait.setOnClickListener(v -> {
                                    if (lottery.addToWaitingList(deviceId)) {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("waitingListIds", lottery.getWaitingListIds());
                                        db.collection("lotteries").document(eventId)
                                                .update(updates)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("joinWaitingList", "waitingList updated successfully");
                                                    loadEventDetails(eventId);
                                                })
                                                .addOnFailureListener(e -> Log.e("joinWaitingList", "Error updating waitingList", e));
                                    }
                                    else {
                                        Toast.makeText(this, "Waiting list is full!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else {
                            Log.w(TAG, "Lottery document not found.");
                            Toast.makeText(this, "Lottery not found.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Log.e(TAG, "Error loading lottery details", e));
                }
            } else {
                Log.w(TAG, "Event document not found.");
                Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading event details", e));
    }

    /**
     * Fetches the organizer's name using the organizerId from the Identity collection.
     *
     * @param organizerId The ID of the organizer.
     */
    private void fetchOrganizerName(String organizerId) {
        if (organizerId != null) {
            DocumentReference identityRef = db.collection("organizers").document(organizerId);
            identityRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String organizerName = documentSnapshot.getString("name");
                    tvOrganizer.setText("Organized by: " + (organizerName != null ? organizerName : "Unknown"));
                } else {
                    tvOrganizer.setText("Organized by: Unknown");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error loading organizer name", e));
        } else {
            tvOrganizer.setText("Organized by: Unknown");
        }
    }
}


