package com.example.frolic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for displaying detailed information about a specific event.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private TextView tvEventName, tvEventDate, tvOrganizer, tvStatus;
    private Button btnWait, btnLeave, btnJoin, btnDecline, btnBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;
    private boolean isGeolocationRequired = false;
    private NotificationHelper notificationHelper;
    private String organizerId;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize notification helper
        notificationHelper = new NotificationHelper();

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
                    organizerId = event.getOrganizerId();
                    fetchOrganizerName(event.getOrganizerId());
                    isGeolocationRequired = event.isGeolocationRequired();

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
                                    lottery.addToConfirmedList(deviceId);
                                    lotteryUpdates.put("confirmedListIds", lottery.getConfirmedListIds());

                                    db.collection("lotteries").document(eventId)
                                            .update(lotteryUpdates)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("joinEntrantsList", "invitedList updated successfully");

                                                // Notify the organizer about the change
                                                db.collection("entrants").document(deviceId)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot3 -> {
                                                            if (documentSnapshot3.exists()) {
                                                                // Get the entrant's name from the document
                                                                String entrantName = documentSnapshot3.getString("name");
                                                                String eventName = tvEventName.getText().toString();
                                                                notificationHelper.addNotification(
                                                                        this,
                                                                        organizerId,
                                                                        entrantName + " has joined your event",
                                                                        entrantName + " has joined your event: " + eventName + "."
                                                                );
                                                                loadEventDetails(eventId);
                                                                if (entrantName != null) {
                                                                    // Do something with the entrant's name
                                                                    Log.d("NotificationHelper", "Entrant Name: " + entrantName);
                                                                } else {
                                                                    Log.e("NotificationHelper", "Entrant name not found.");
                                                                }
                                                            } else {
                                                                Log.e("NotificationHelper", "Entrant document does not exist.");
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("NotificationHelper", "Error retrieving entrant name: " + e.getMessage());
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("joinEntrantsList", "Error updating invitedList", e));
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

                                                // Notify the organizer about the change
                                                db.collection("entrants").document(deviceId)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot3 -> {
                                                            if (documentSnapshot3.exists()) {
                                                                // Get the entrant's name from the document
                                                                String entrantName = documentSnapshot3.getString("name");
                                                                String eventName = tvEventName.getText().toString();
                                                                notificationHelper.addNotification(
                                                                        this,
                                                                        organizerId,
                                                                        "User declined your invitation",
                                                                        entrantName + " has declined your invitation for event: " + eventName + "."
                                                                );
                                                                loadEventDetails(eventId);
                                                                if (entrantName != null) {
                                                                    // Do something with the entrant's name
                                                                    Log.d("NotificationHelper", "Entrant Name: " + entrantName);
                                                                } else {
                                                                    Log.e("NotificationHelper", "Entrant name not found.");
                                                                }
                                                            } else {
                                                                Log.e("NotificationHelper", "Entrant document does not exist.");
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("NotificationHelper", "Error retrieving entrant name: " + e.getMessage());
                                                        });
                                            })
                                            .addOnFailureListener(e -> Log.e("declineInvitation", "Error updating invitedListIds and canceledListIds", e));
                                });

                            }
                            else if (lottery.getConfirmedListIds().contains(deviceId)) {
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
                                    if (isGeolocationRequired) {
                                        checkLocationPermissionAndJoinWaitlist(lottery, deviceId);
                                    } else if (lottery.addToWaitingList(deviceId)) {
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
     * Checks for location permissions and services, and allows a user to join the waiting list with their location.
     *
     * @param lottery  The lottery system instance associated with the event.
     * @param deviceId The device ID of the user attempting to join the waiting list.
     */
    private void checkLocationPermissionAndJoinWaitlist(LotterySystem lottery, String deviceId) {
        // Show a warning dialog to the user
        new AlertDialog.Builder(this)
                .setTitle("Location Required")
                .setMessage("This event requires your location to join the waiting list. Your location will be stored securely and used only for this event.")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    // Check if location permissions are granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // Request location permissions
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                        return;
                    }

                    // Check if location services are enabled
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(this, "Please enable location services to join the waiting list.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        return;
                    }

                    // Retrieve the user's current location
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(location -> {
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    // Add the entrant to the waiting list
                                    if (lottery.addToWaitingList(deviceId)) {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("waitingListIds", lottery.getWaitingListIds());

                                        // Save location to Firestore alongside waiting list update
                                        updates.put("entrantLocations." + deviceId, new GeoPoint(latitude, longitude));

                                        db.collection("lotteries").document(eventId)
                                                .update(updates)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("joinWaitingList", "Waiting list and location updated successfully");
                                                    Toast.makeText(this, "Successfully joined the waiting list with location!", Toast.LENGTH_SHORT).show();
                                                    loadEventDetails(eventId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("joinWaitingList", "Error updating waiting list or location", e);
                                                    Toast.makeText(this, "Error joining the waiting list.", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(this, "Waiting list is full!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Unable to retrieve location. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("LocationError", "Error retrieving location", e);
                                Toast.makeText(this, "Error retrieving your location. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
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


