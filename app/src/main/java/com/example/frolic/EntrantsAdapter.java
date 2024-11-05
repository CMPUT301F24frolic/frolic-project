package com.example.frolic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import java.util.ArrayList;

/**
 * Adapter class for displaying entrant details in a RecyclerView. This adapter
 * retrieves entrant information, including name and email, from Firestore using
 * a list of entrant IDs.
 * Used in the Organizer's view of entrants in their event
 */
public class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.EntrantViewHolder> {

    private ArrayList<String> entrantIds;
    private FirebaseFirestore db;

    /**
     * Constructs an EntrantsAdapter with a list of entrant IDs.
     * Initializes a Firebase Firestore instance for fetching entrant details.
     *
     * @param entrantIds List of entrant IDs used to query Firestore for entrant details
     */
    public EntrantsAdapter(ArrayList<String> entrantIds) {
        this.entrantIds = entrantIds;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent The parent view group
     * @param viewType The view type of the new View
     * @return A new instance of EntrantViewHolder containing the inflated view
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrant, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Binds the entrant data from Firestore to the ViewHolder. Fetches entrant name and email
     * based on entrant ID, and updates the TextViews in the ViewHolder. Handles cases where
     * entrant data is missing or fetch fails.
     *
     * @param holder The ViewHolder containing views to bind the data
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String entrantId = entrantIds.get(position);

        // Fetch entrant details from Firebase based on entrantId
        DocumentReference entrantRef = db.collection("entrants").document(entrantId);
        entrantRef.get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Set the name and email on the TextViews
                holder.tvEntrantName.setText(documentSnapshot.getString("name"));
                holder.tvEntrantEmail.setText(documentSnapshot.getString("email"));
            } else {
                // If no data found, set default text
                holder.tvEntrantName.setText("Unknown");
                holder.tvEntrantEmail.setText("No email available");
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
            holder.tvEntrantName.setText("Error loading");
            holder.tvEntrantEmail.setText("Error loading email");
        });
    }

    /**
     * Returns the total number of entrant IDs in the list.
     *
     * @return Size of the entrant IDs list
     */
    @Override
    public int getItemCount() {
        return entrantIds.size();
    }

    /**
     * Updates the list of entrant IDs used in the adapter and refreshes the RecyclerView.
     *
     * @param newEntrantsList New list of entrant IDs
     */
    public void updateEntrantsList(ArrayList<String> newEntrantsList) {
        this.entrantIds.clear();
        this.entrantIds.addAll(newEntrantsList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for managing individual entrant views within the RecyclerView.
     * Holds references to the name and email TextViews.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntrantName, tvEntrantEmail;

        /**
         * Initializes TextView references for entrant name and email.
         *
         * @param itemView The view of the individual list item
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEntrantName = itemView.findViewById(R.id.tvEntrantName);
            tvEntrantEmail = itemView.findViewById(R.id.tvEntrantEmail);
        }
    }
}
