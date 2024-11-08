package com.example.frolic;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Adapter for displaying user profiles in the admin management interface.
 * Provides functionality for viewing and removing user profiles.
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.ViewHolder> {
    private ArrayList<Identity> profiles;
    private OnProfileActionListener listener;
    private Context context;

    /**
     * Interface for handling profile-related actions in the admin view.
     */
    public interface OnProfileActionListener {
        void onProfileDeleted(String userId, String userType);
        void onProfileSelected(Identity profile);
    }

    /**
     * ViewHolder class for profile items in the RecyclerView.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntrantName;
        TextView tvEntrantEmail;

        ViewHolder(View itemView) {
            super(itemView);
            tvEntrantName = itemView.findViewById(R.id.tvEntrantName);
            tvEntrantEmail = itemView.findViewById(R.id.tvEntrantEmail);
        }
    }

    /**
     * Constructor for the adapter.
     */
    public AdminProfilesAdapter(ArrayList<Identity> profiles, OnProfileActionListener listener, Context context) {
        this.profiles = profiles;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entrant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Identity profile = profiles.get(position);

        holder.tvEntrantName.setText(profile.getName() != null && !profile.getName().isEmpty() ?
                profile.getName() : "No Name");
        holder.tvEntrantEmail.setText(profile.getEmail() != null && !profile.getEmail().isEmpty() ?
                profile.getEmail() : "No Email");

        holder.itemView.setOnClickListener(v -> {
            Log.d("AdminProfilesAdapter", "Profile clicked: " + profile.getDeviceID());
            new AlertDialog.Builder(context)
                    .setTitle("Profile Actions")
                    .setItems(new CharSequence[]{"View Details", "Delete Profile", "Cancel"},
                            (dialog, which) -> {
                                switch (which) {
                                    case 0: // View Details
                                        showProfileDetails(profile);
                                        break;
                                    case 1: // Delete Profile
                                        if (listener != null) {
                                            listener.onProfileDeleted(profile.getDeviceID(),
                                                    profile.getRole().toLowerCase());
                                        }
                                        break;
                                    case 2: // Cancel
                                        dialog.dismiss();
                                        break;
                                }
                            })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return profiles != null ? profiles.size() : 0;
    }

    /**
     * Updates the list of profiles and refreshes the display.
     */
    public void updateProfiles(ArrayList<Identity> newProfiles) {
        this.profiles = newProfiles;
        notifyDataSetChanged();
    }

    /**
     * Shows profile details in a dialog.
     */
    private void showProfileDetails(Identity profile) {
        String details = String.format(
                "Name: %s\n\n" +
                        "Email: %s\n\n" +
                        "Role: %s\n\n" +
                        "Phone: %s\n\n" +
                        "Admin Access: %s\n\n" +
                        "Notifications: %s",
                profile.getName() != null ? profile.getName() : "Not provided",
                profile.getEmail() != null ? profile.getEmail() : "Not provided",
                profile.getRole() != null ? profile.getRole() : "Not specified",
                profile.getPhoneNumber() != 0 ? String.valueOf(profile.getPhoneNumber()) : "Not provided",
                profile.getAdmin() ? "Yes" : "No",
                profile.getNotifications() ? "Enabled" : "Disabled"
        );

        new AlertDialog.Builder(context)
                .setTitle("Profile Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    if (listener != null) {
                        listener.onProfileDeleted(profile.getDeviceID(),
                                profile.getRole().toLowerCase());
                    }
                })
                .show();
    }
}