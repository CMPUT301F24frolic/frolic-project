package com.example.frolic;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

/**
 * Adapter for displaying and managing facilities in the admin interface.
 * Provides functionality for viewing facility details and managing policy violations.
 */
public class AdminFacilitiesAdapter extends RecyclerView.Adapter<AdminFacilitiesAdapter.ViewHolder> {
    private static final String TAG = "AdminFacilitiesAdapter";

    /**
     * Helper class to temporarily hold facility data from Firebase
     */
    public static class FacilityData {
        String id;
        String name;
        String address;
        String organizerId;

        FacilityData(String id, String name, String address, String organizerId) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.organizerId = organizerId;
        }
    }

    private ArrayList<FacilityData> facilitiesData;
    private Context context;
    private FirebaseFirestore db;

    /**
     * ViewHolder class for facility items in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, organizerName;

        ViewHolder(View itemView) {
            super(itemView);
            facilityName = itemView.findViewById(R.id.tvEntrantName);
            organizerName = itemView.findViewById(R.id.tvEntrantEmail);
        }
    }

    /**
     * Constructs an AdminFacilitiesAdapter.
     * @param facilitiesData List of facilities to display
     * @param context The activity context
     */
    public AdminFacilitiesAdapter(ArrayList<FacilityData> facilitiesData, Context context) {
        this.facilitiesData = facilitiesData;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FacilityData facilityData = facilitiesData.get(position);
        holder.facilityName.setText(facilityData.name);

        // Fetch organizer name from Firebase
        db.collection("organizers").document(facilityData.organizerId)
                .get()
                .addOnSuccessListener(document -> {
                    String organizerName = document.getString("name");
                    holder.organizerName.setText("Organizer: " +
                            (organizerName != null ? organizerName : "Unknown"));
                })
                .addOnFailureListener(e -> {
                    holder.organizerName.setText("Organizer: Unknown");
                    Log.e(TAG, "Error fetching organizer details", e);
                });

        holder.itemView.setOnClickListener(v -> showFacilityActions(facilityData));
    }

    @Override
    public int getItemCount() {
        return facilitiesData.size();
    }

    private void showFacilityActions(FacilityData facilityData) {
        new AlertDialog.Builder(context)
                .setTitle("Facility Actions")
                .setItems(new CharSequence[]{
                        "View Details",
                        "Remove Facility",
                        "Cancel"
                }, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showFacilityDetails(facilityData);
                            break;
                        case 1:
                            confirmFacilityRemoval(facilityData);
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void showFacilityDetails(FacilityData facilityData) {
        // Fetch full organizer details for display
        db.collection("organizers").document(facilityData.organizerId)
                .get()
                .addOnSuccessListener(document -> {
                    String organizerName = document.getString("name");
                    String organizerEmail = document.getString("email");

                    String details = String.format(
                            "Facility Name: %s\n\n" +
                                    "Address: %s\n\n" +
                                    "Organizer: %s\n\n" +
                                    "Organizer Email: %s",
                            facilityData.name,
                            facilityData.address,
                            organizerName != null ? organizerName : "Unknown",
                            organizerEmail != null ? organizerEmail : "Unknown"
                    );

                    new AlertDialog.Builder(context)
                            .setTitle("Facility Details")
                            .setMessage(details)
                            .setPositiveButton("OK", null)
                            .show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching organizer details", e);
                    Toast.makeText(context, "Error loading facility details",
                            Toast.LENGTH_SHORT).show();
                });
    }


    private void confirmFacilityRemoval(FacilityData facilityData) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Facility")
                .setMessage("Are you sure you want to remove this facility? This action cannot be undone.")
                .setPositiveButton("Remove", (dialog, which) -> removeFacility(facilityData))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeFacility(FacilityData facilityData) {
        db.collection("facilities").document(facilityData.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    facilitiesData.remove(facilityData);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Facility removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error removing facility", e);
                    Toast.makeText(context, "Failed to remove facility", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the adapter's facility list.
     * @param newFacilities New list of facilities to display
     */
    public void updateFacilities(ArrayList<FacilityData> newFacilities) {
        this.facilitiesData = newFacilities;
        notifyDataSetChanged();
    }
}