package com.example.frolic;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.frolic.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

/**
 * Activity for displaying entrant locations on a Google Map for a specific event.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FirebaseFirestore db;
    private String eventId;

    /**
     * Called when the activity is first created. Sets up the layout and initializes Firestore.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Callback triggered when the Google Map is ready to be used.
     *
     * @param googleMap The Google Map object that is ready for use.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        if (eventId != null) {
            fetchEntrantLocations(eventId);
        } else {
            Log.e(TAG, "Event ID is null. Cannot fetch entrant locations.");
        }
    }

    /**
     * Fetches entrant locations from Firestore and displays them on the map.
     *
     * @param eventId The event ID whose entrant locations are to be fetched.
     */
    private void fetchEntrantLocations(String eventId) {
        DocumentReference lotteryRef = db.collection("lotteries").document(eventId);
        lotteryRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> entrantLocations = (Map<String, Object>) documentSnapshot.get("entrantLocations");
                if (entrantLocations != null) {
                    for (Map.Entry<String, Object> entry : entrantLocations.entrySet()) {
                        String entrantId = entry.getKey();
                        GeoPoint location = (GeoPoint) entry.getValue();

                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title("Entrant ID: " + entrantId));
                        }
                    }

                    // Move the camera to the first entrant's location
                    if (!entrantLocations.isEmpty()) {
                        Map.Entry<String, Object> firstEntry = entrantLocations.entrySet().iterator().next();
                        GeoPoint firstLocation = (GeoPoint) firstEntry.getValue();
                        LatLng firstLatLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10));
                    }
                } else {
                    Log.e(TAG, "No entrant locations found for eventId: " + eventId);
                }
            } else {
                Log.e(TAG, "Lottery document does not exist for eventId: " + eventId);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching entrant locations: ", e));
    }
}
