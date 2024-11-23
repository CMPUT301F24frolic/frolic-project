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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Adapter class for managing and displaying events in the admin interface.
 * Provides functionality for viewing event details and managing event operations
 * such as deletion. Implements a dialog-based interaction pattern for event management.
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {
    private ArrayList<Event> events;
    private OnEventActionListener listener;
    private Context context;
    private FirebaseFirestore db;

    /**
     * Interface defining callbacks for event-related actions in the admin view.
     */
    public interface OnEventActionListener {
        /**
         * Called when an event is selected for deletion.
         * @param eventId The ID of the event to be deleted
         */
        void onEventDeleted(String eventId);

        /**
         * Called when an event is selected for viewing or editing.
         * @param event The Event object that was selected
         */
        void onEventSelected(Event event);
    }

    /**
     * ViewHolder class for event items in the RecyclerView.
     * Holds references to the views that display event information.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate;

        /**
         * Constructs a ViewHolder with references to the event item views.
         * @param itemView The view containing the event item layout
         */
        ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.tvEventName);
            eventDate = itemView.findViewById(R.id.tvOrganizerName);
        }
    }

    /**
     * Constructs an AdminEventsAdapter with the specified events list and action listener.
     *
     * @param events List of events to display
     * @param listener Listener for event-related actions
     * @param context The activity context
     */
    public AdminEventsAdapter(ArrayList<Event> events, OnEventActionListener listener, Context context) {
        this.events = events;
        this.listener = listener;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.eventName.setText(event.getEventName() != null ?
                event.getEventName() : "Unnamed Event");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        holder.eventDate.setText(event.getEventDate() != null ?
                dateFormat.format(event.getEventDate()) : "No date set");

        holder.itemView.setOnClickListener(v -> showActionDialog(event));
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    /**
     * Updates the adapter's event list and refreshes the display.
     *
     * @param newEvents New list of events to display
     */
    public void updateEvents(ArrayList<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    /**
     * Displays an action dialog for the selected event.
     * Provides options to view details or delete the event.
     *
     * @param event The event for which to show actions
     */
    private void showActionDialog(Event event) {
        new AlertDialog.Builder(context)
                .setTitle("Event Actions")
                .setItems(new CharSequence[]{
                                "View Details",
                                "Manage QR Code",
                                "Delete Event",
                                "Cancel"},
                        (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showEventDetails(event);
                                    break;
                                case 1:
                                    showQRCodeManagementDialog(event);
                                    break;
                                case 2:
                                    if (listener != null) {
                                        listener.onEventDeleted(event.getEventId());
                                    }
                                    break;
                                case 3:
                                    dialog.dismiss();
                                    break;
                            }
                        })
                .show();
    }

    /**
     * Displays a dialog showing detailed information about an event.
     * Includes event name, date, capacity limits, and configuration settings.
     *
     * @param event The event whose details should be displayed
     */
    private void showEventDetails(Event event) {
        String details = String.format(
                "Event Name: %s\n\n" +
                        "Date: %s\n\n" +
                        "Max Attendees: %d\n\n" +
                        "Waitlist Limit: %d\n\n" +
                        "Geolocation Required: %s\n\n" +
                        "Notifications: %s",
                event.getEventName(),
                new SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(event.getEventDate()),
                event.getMaxConfirmed(),
                event.getWaitlistLimit(),
                event.isGeolocationRequired() ? "Yes" : "No",
                event.isReceiveNotification() ? "Enabled" : "Disabled"
        );

        new AlertDialog.Builder(context)
                .setTitle("Event Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    if (listener != null) {
                        listener.onEventDeleted(event.getEventId());
                    }
                })
                .show();
    }

    /**
     * Shows a dialog for managing the QR code of an event.
     * Allows viewing QR information and removing the QR hash.
     * @param event The event whose QR code is to be managed
     */
    private void showQRCodeManagementDialog(Event event) {
        String qrHash = event.getQrHash();
        String message = qrHash != null && !qrHash.isEmpty() ?
                "Current QR Hash: " + qrHash :
                "No QR hash associated with this event";

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("QR Code Management")
                .setMessage(message);

        if (qrHash != null && !qrHash.isEmpty()) {
            builder.setPositiveButton("Remove QR Hash", (dialog, which) -> {
                removeQRHash(event);
            });
        }

        builder.setNegativeButton("Close", null)
                .show();
    }

    /**
     * Removes the QR hash from an event in the database.
     * @param event The event whose QR hash should be removed
     */
    private void removeQRHash(Event event) {
        db.collection("events").document(event.getEventId())
                .update("qrHash", null)
                .addOnSuccessListener(aVoid -> {
                    event.setQrHash(null);
                    Toast.makeText(context,
                            "QR hash removed successfully",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminEventsAdapter", "Error removing QR hash", e);
                    Toast.makeText(context,
                            "Failed to remove QR hash",
                            Toast.LENGTH_SHORT).show();
                });
    }
}