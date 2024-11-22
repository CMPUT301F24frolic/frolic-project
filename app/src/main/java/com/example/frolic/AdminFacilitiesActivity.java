package com.example.frolic;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;


/**
 * Activity for administrators to manage facilities.
 * Provides functionality for viewing facilities, managing violations,
 * and removing facilities that violate policies.
 */
public class AdminFacilitiesActivity extends AppCompatActivity {
    private static final String TAG = "AdminFacilitiesActivity";
    private FirebaseFirestore db;
    private RecyclerView rvFacilities;
    private AdminFacilitiesAdapter adapter;
    private ArrayList<AdminFacilitiesAdapter.FacilityData> facilityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facilities_screen);

        db = FirebaseFirestore.getInstance();
        facilityList = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        loadFacilities();
    }

    /**
     * Initializes view references and sets up basic UI interactions.
     */
    private void initializeViews() {
        rvFacilities = findViewById(R.id.rvFacilities);
        TextView tvBack = findViewById(R.id.tvBackManageFacilities);
        tvBack.setOnClickListener(v -> finish());
    }

    /**
     * Sets up the RecyclerView with its adapter and layout manager.
     */
    private void setupRecyclerView() {
        rvFacilities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminFacilitiesAdapter(facilityList, this);
        rvFacilities.setAdapter(adapter);
    }

    /**
     * Loads facilities from Firestore database.
     * Constructs Facility objects by fetching associated Organizer data.
     */
    private void loadFacilities() {
        db.collection("facilities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<AdminFacilitiesAdapter.FacilityData> facilitiesData = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String address = document.getString("address");
                        String organizerId = document.getString("organizerId");

                        facilitiesData.add(new AdminFacilitiesAdapter.FacilityData(
                                id, name, address, organizerId));
                    }
                    adapter.updateFacilities(facilitiesData);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading facilities", e);
                    Toast.makeText(this, "Failed to load facilities", Toast.LENGTH_SHORT).show();
                });
    }
}